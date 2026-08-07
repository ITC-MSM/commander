package com.wingedsheep.ai.engine

import com.wingedsheep.ai.engine.advisor.CardAdvisorModule
import com.wingedsheep.ai.engine.advisor.modules.BloomburrowAdvisorModule
import com.wingedsheep.ai.engine.advisor.modules.OnslaughtAdvisorModule
import com.wingedsheep.ai.engine.budget.BudgetPolicy
import com.wingedsheep.ai.engine.budget.LegacyBudgetPolicy
import com.wingedsheep.ai.engine.budget.TieredBudgetPolicy
import com.wingedsheep.ai.engine.evaluation.EvalWeights
import com.wingedsheep.ai.engine.rollout.RolloutSettings

/**
 * A named, reproducible configuration of the engine AI.
 *
 * This is the versioning seam the arena measures against, and the switchboard the later phases of
 * `backlog/engine-ai-improvement.md` hang their features off (candidate evaluator, rollout counts,
 * determinizations, budget overrides). Today it carries only what is actually wired — a profile
 * field that nothing reads would be a lie about what a run measured.
 *
 * **[LEGACY_V0] is the permanent reference opponent.** Every later version reports its win rate
 * against it, so the numbers stay comparable across months. It must never be "improved"; if a
 * refactor moves it, `FrozenBaselineTest` fails, which is the whole point of that test.
 */
data class AiProfile(
    /** Stable identifier. Appears in arena reports and CSV rows; treat it as part of the API. */
    val id: String,
    /**
     * Per-set card advisor modules. Modules and the advisors they register are stateless
     * singletons, so one profile instance is safe to share across threads and games.
     */
    val advisorModules: List<CardAdvisorModule> = emptyList(),
    /** Resource-backed leaf-evaluation vector. Unknown ids use the compiled default safely. */
    val evalWeightsId: String = EvalWeights.DEFAULT_ID,
    /**
     * Phase 4a: only propose actions the AI can actually take, and skip windows where it has
     * none.
     *
     * Routes candidate generation and whole-window skipping through
     * [com.wingedsheep.engine.legalactions.MeaningfulActionFilter] — the same rules the client's
     * auto-pass uses — and fixes the Strategist's target filling to fill the slots it can instead
     * of abandoning a spell whose *optional* slot happens to be empty. Off for [LEGACY_V0]:
     * the second half is a plain bug fix, but the reference opponent has to stay frozen or every
     * number published against it silently rebases.
     */
    val useMeaningfulFilter: Boolean = false,
    /**
     * Phase 4b. How much search one decision may spend. [LegacyBudgetPolicy] is today's
     * constants with no global deadline.
     */
    val budgetPolicy: BudgetPolicy = LegacyBudgetPolicy,
    /**
     * Phase 6: structural card knowledge
     * ([com.wingedsheep.ai.engine.knowledge.CardIntent]).
     *
     * Turns on three consumers at once — `BoardPresence.permanentValue`'s flat `0.5` for every
     * non-creature permanent, `Strategist.heuristicTargetRank`'s flat `0.0` for one, and the
     * intent-driven [com.wingedsheep.ai.engine.knowledge.HoldPolicy] that replaces the hardcoded
     * end-step discount. Off for [LEGACY_V0]: the reference opponent has to stay frozen or every
     * number published against it silently rebases.
     */
    val useCardIntent: Boolean = false,
    /**
     * Phase 7: score a candidate by the mean of several short playouts instead of one static
     * evaluation. Null is the off position and leaves the greedy 1-ply leaf in place.
     *
     * This is the plan's primary strength lever, and the only phase so far whose job is to move a
     * win rate rather than enable one. Off for [LEGACY_V0] for the usual reason — the reference
     * opponent has to stay frozen — and off for [CURRENT] until the arena says it should ship.
     *
     * How *many* playouts a decision gets comes from the budget tier, not from here; see
     * [com.wingedsheep.ai.engine.budget.SearchAllowances.rolloutPlayouts]. A profile with rollouts
     * on and [LegacyBudgetPolicy] still gets them, at the nominal NORMAL count — which is what
     * makes `v0-rollout` an attributable one-variable change from `v0`.
     */
    val rollouts: RolloutSettings? = null,
    /**
     * Phase 8: sample opponent hand identities and library order before rollout evaluation.
     * Off preserves the historical full-information agents used as arena controls.
     */
    val determinizeHiddenInformation: Boolean = false,
    /**
     * Carry a candidate simulation through the combat damage step once blockers are declared,
     * instead of stopping at the empty stack that sits one step before it.
     *
     * The cheap half of what Phase 7's rollouts were bought for. Three of the suite's six
     * remaining failures pay mana now for a payoff that only lands at damage — a Fog, a
     * regeneration shield, a firebreathing pump — and a one-ply evaluator that stops at the empty
     * stack sees only the cost. Unlike a rollout this adds no sampling and no horizon *beyond*
     * combat, so it cannot manufacture the tempo blindness that made `v0-rollout` lose
     * `respond-02`.
     *
     * See [GameSimulator]'s constructor for why it starts at declare-blockers rather than
     * declare-attackers.
     */
    val resolveThroughCombatDamage: Boolean = false,
    /**
     * Charge an attack plan's estimated crack-back as the life it would actually cost, rather
     * than a flat −3.0 that only fires when the crack-back is exactly lethal.
     *
     * See [CombatAdvisor]'s constructor. The target is `race-03` — "keep the ground creature home
     * to block the 3/3" — which is the one attack-vs-hold position the evaluator owns rather than
     * `CombatSeed`, and which the rollouts do not close either.
     */
    val priceCrackBackAsLife: Boolean = false,
    /**
     * Stop charging a land drop as card loss. A land moving from hand to battlefield is not a card
     * spent, it is a card converted to mana — and mana is what [EvaluationWeights.tempo] and
     * `BoardPresence` already price.
     *
     * See [com.wingedsheep.ai.engine.evaluation.CardAdvantage]'s `heldCardCount`. The target is
     * `sequencing-02` — "on the last card in hand, still make the land drop" — the one puzzle every
     * profile measured so far fails, `production` and `production-candidate-tuned` included. It is
     * also the one whose real-game frequency is highest: any turn whose only card in hand is a land
     * is a turn the AI skips its land drop, and every game reaches several.
     *
     * Not the same lever as [EvaluationWeights.topdeckPenalty], which the promotion run reached for
     * and rejected. Moving the constant to −1.0 also closes this puzzle, but by making an empty hand
     * cheaper *everywhere* — which is why it cost `respond-02`. This changes what a hand *contains*
     * and leaves the cliff exactly where it is.
     */
    val landDropIsNotCardLoss: Boolean = false,
    /** Non-null profiles may only be selected automatically for this set. Arena selection stays explicit. */
    val restrictedToSet: String? = null,
) {
    companion object {
        /**
         * The frozen reference opponent: greedy 1-ply, default weights, no card advisors.
         * Pinned as of Phase 1 (2026-07-27). **Do not change this.**
         */
        val LEGACY_V0 = AiProfile(id = "v0")

        /**
         * What `AIPlayer.create(registry, playerId)` builds today. Identical to [LEGACY_V0] right
         * now — it diverges as later phases turn features on, and the gap between the two is
         * exactly what the arena measures.
         */
        val CURRENT = AiProfile(id = "current")

        /**
         * What a player faced in a real game **up to 2026-08-07**, when
         * [PRODUCTION_CANDIDATE_TUNED] replaced it in [EngineAiPlayerController].
         *
         * Kept, unchanged, as the baseline every promotion is measured against — the same job
         * [LEGACY_V0] does for the plan's phases, one level up. A candidate has to beat *this*,
         * not `v0`, because `v0` carries neither the card advisors nor `CardIntent` that shipped
         * long ago. It is still what `PuzzleSuiteTest` runs, so `KNOWN_FAILURES` keeps describing
         * a fixed agent rather than drifting with whatever is live.
         */
        val PRODUCTION = AiProfile(
            id = "production",
            advisorModules = listOf(BloomburrowAdvisorModule(), OnslaughtAdvisorModule()),
            useCardIntent = true,
        )

        /**
         * What [PRODUCTION] would become if Phases 4, 7 and 8 were switched on for real players:
         * the shipped card advisors and `CardIntent`, plus the meaningful-action filter, the
         * four-tier budget, the rollout evaluator and fair play. The arena agent
         * `production-candidate`.
         *
         * **This is the only profile that answers the promotion question**, and until it existed
         * nothing did. Every number the plan publishes is quoted against [LEGACY_V0], which carries
         * neither advisors nor `CardIntent` — so "the rollouts are worth +6%" is a statement about
         * an agent nobody plays against. The gate is `just arena production production-candidate`;
         * the compounding check is `just arena v0 production-candidate`.
         *
         * [TieredBudgetPolicy] is not optional here, unlike in the phase-isolating profiles.
         * [LegacyBudgetPolicy] has no global deadline, so rollouts under it are a search a real
         * player waits on with nothing to stop it — acceptable in an arena that spends its budget
         * as a simulation count, not in a session with a human on the other end.
         */
        val PRODUCTION_CANDIDATE = PRODUCTION.copy(
            id = "production-candidate",
            useMeaningfulFilter = true,
            budgetPolicy = TieredBudgetPolicy(),
            rollouts = RolloutSettings.DEFAULT,
            determinizeHiddenInformation = true,
        )

        /**
         * The two targeted fixes for the suite's remaining failures, without rollouts:
         * the combat-damage horizon and the concave hand curve, on top of what already ships.
         *
         * Between them they aim at four of `PuzzleSuiteTest.KNOWN_FAILURES` — `instants-05` and
         * `activate-05` from the horizon, `sequencing-02` and `noncreature-02` from the curve —
         * and they cost essentially nothing: one extra step of simulation inside combat, and a
         * different constant. That makes this the control that says whether
         * [PRODUCTION_CANDIDATE]'s rollouts are still paying for themselves once the cheap fixes
         * are in, rather than being credited for what a constant would have bought.
         */
        val PRODUCTION_TUNED = PRODUCTION.copy(
            id = "production-tuned",
            useMeaningfulFilter = true,
            budgetPolicy = TieredBudgetPolicy(),
            evalWeightsId = "concave-hand",
            resolveThroughCombatDamage = true,
        )

        /**
         * Attribution controls for [PRODUCTION_TUNED], which moves four things at once. Each of
         * these moves exactly one, so a puzzle that changes can be blamed on something.
         */
        val PRODUCTION_HORIZON = PRODUCTION.copy(
            id = "production-horizon",
            resolveThroughCombatDamage = true,
        )
        val PRODUCTION_CONCAVE = PRODUCTION.copy(
            id = "production-concave",
            evalWeightsId = "concave-hand",
        )

        /** The same curve fix at half strength: empty hand at −2.0 rather than −1.0. */
        val PRODUCTION_CONCAVE_2 = PRODUCTION.copy(
            id = "production-concave-2",
            evalWeightsId = "concave-hand-2",
        )

        /** The crack-back pricing on its own, so `race-03` moving can be attributed to it. */
        val PRODUCTION_CRACKBACK = PRODUCTION.copy(
            id = "production-crackback",
            priceCrackBackAsLife = true,
        )

        /** The land-drop accounting on its own, so `sequencing-02` moving can be attributed to it. */
        val PRODUCTION_LANDDROP = PRODUCTION.copy(
            id = "production-landdrop",
            landDropIsNotCardLoss = true,
        )

        /** All three targeted fixes. The best the suite can be pushed to without curve-fitting. */
        val PRODUCTION_TARGETED = PRODUCTION.copy(
            id = "production-targeted",
            evalWeightsId = "concave-hand-2",
            resolveThroughCombatDamage = true,
            priceCrackBackAsLife = true,
        )

        /** Horizon plus each curve value, to pick the pair that costs nothing. */
        val PRODUCTION_HORIZON_CONCAVE = PRODUCTION.copy(
            id = "production-horizon-concave",
            evalWeightsId = "concave-hand",
            resolveThroughCombatDamage = true,
        )
        val PRODUCTION_HORIZON_CONCAVE_2 = PRODUCTION.copy(
            id = "production-horizon-concave-2",
            evalWeightsId = "concave-hand-2",
            resolveThroughCombatDamage = true,
        )

        /**
         * What a player faced in a real game **from 2026-08-07 until 2026-08-08**, when
         * [PRODUCTION_CANDIDATE_LANDDROP] replaced it in [EngineAiPlayerController]. Kept unchanged
         * as the baseline that promotion was measured against — the same job [PRODUCTION] does for
         * this one.
         *
         * [PRODUCTION_CANDIDATE] with the two cheap fixes on top. The two scoreboards turned out
         * to measure nearly orthogonal things, which is why this is a combination rather than a
         * choice. The rollouts win the arena (**+7.3%** against `production` over 300 paired
         * games) and *cost* four puzzles; the combat-damage horizon and the concave hand curve win
         * three puzzles and are arena-neutral (49.7%, CI [48.7%, 50.7%]). Neither result argues
         * against the other, so this takes both — measured together at **+6.7%**
         * (`production` 43.3%, CI [39.3%, 47.0%]) and 60/66 on the suite, level with `production`.
         *
         * `concave-hand-2` rather than `concave-hand`: −1.0 also closes `sequencing-02`, but it
         * starts spending the last Counterspell on a 2/2 with seven lands open, and `respond-02`
         * is the negative control that exists to catch exactly that.
         *
         * The id stays `production-candidate-tuned` after promotion on purpose — every arena
         * report and CSV row already published under that name refers to this exact agent, and
         * renaming it to match its new status would break that.
         */
        val PRODUCTION_CANDIDATE_TUNED = PRODUCTION_CANDIDATE.copy(
            id = "production-candidate-tuned",
            evalWeightsId = "concave-hand-2",
            resolveThroughCombatDamage = true,
        )

        /**
         * **What a player faces in a real game as of 2026-08-08** — see [EngineAiPlayerController].
         *
         * [PRODUCTION_CANDIDATE_TUNED] plus [landDropIsNotCardLoss] — the agent that finally makes
         * its land drop. Promoted on the same bar the two fixes above it were: **arena-neutral and
         * a puzzle ahead**. 300 paired games against `production-candidate-tuned` measured 49.0%,
         * CI [46.3%, 51.3%], 300/300 completed, 0 illegal actions; the accounting on its own
         * (`production` vs `production-landdrop`, 400 games) 49.5%, CI [46.8%, 52.3%]. On the
         * 66-puzzle suite it closes `sequencing-02` — the only puzzle every profile before it
         * failed — and moves no other verdict, for the live pair and for `production` alike.
         *
         * A new id rather than a flag flipped on the live profile, for the reason
         * [PRODUCTION_CANDIDATE_TUNED]'s own KDoc gives: every arena report already published under
         * that name refers to that exact agent, and the baseline a promotion is measured against
         * cannot be the thing being promoted.
         */
        val PRODUCTION_CANDIDATE_LANDDROP = PRODUCTION_CANDIDATE_TUNED.copy(
            id = "production-candidate-landdrop",
            landDropIsNotCardLoss = true,
        )

        /**
         * Everything Phase 4 added, on top of [LEGACY_V0]: the meaningful-action filter and the
         * four-tier decision budget at its nominal sizes. The arena agent `v0-phase4`.
         */
        val PHASE4 = AiProfile(
            id = "v0-phase4",
            useMeaningfulFilter = true,
            budgetPolicy = TieredBudgetPolicy(),
        )

        /**
         * Phase 6's card knowledge alone, on top of [LEGACY_V0]. The arena agent `v0-intent`:
         * `just arena v0 v0-intent 1000` is the phase's merge gate, and isolating it from Phase 4
         * is what makes that number attributable.
         */
        val PHASE6 = AiProfile(
            id = "v0-intent",
            useCardIntent = true,
        )

        /** Everything Phases 4 and 6 add — what the plan proposes to ship. */
        val PHASE4_PHASE6 = AiProfile(
            id = "v0-phase4-intent",
            useMeaningfulFilter = true,
            budgetPolicy = TieredBudgetPolicy(),
            useCardIntent = true,
        )

        /**
         * Phase 7's rollout evaluator alone, on top of [LEGACY_V0]. The arena agent `v0-rollout`:
         * `just arena v0 v0-rollout 1000` is the phase's merge gate, and isolating it from Phases 4
         * and 6 is what makes that number attributable to the rollouts.
         */
        val PHASE7 = AiProfile(
            id = "v0-rollout",
            rollouts = RolloutSettings.DEFAULT,
        )

        /** Phase 7 search over a fair, sampled hidden-information state. */
        val PHASE8 = AiProfile(
            id = "v0-rollout-determinized",
            rollouts = RolloutSettings.DEFAULT,
            determinizeHiddenInformation = true,
        )

        /** Everything Phases 4, 6 and 7 add — what the plan proposes to ship. */
        val PHASE4_PHASE6_PHASE7 = AiProfile(
            id = "v0-phase4-intent-rollout",
            useMeaningfulFilter = true,
            budgetPolicy = TieredBudgetPolicy(),
            useCardIntent = true,
            rollouts = RolloutSettings.DEFAULT,
        )

        val ECL_APPRENTICE = PRODUCTION.copy(
            id = "ecl-apprentice",
            evalWeightsId = "ecl-apprentice",
            restrictedToSet = "ECL",
        )

        val ECL_OVERLAY = PRODUCTION.copy(
            id = "ecl-overlay",
            evalWeightsId = "ecl-overlay",
            restrictedToSet = "ECL",
        )
    }
}

/** The only automatic promotion seam: a set-scoped profile cannot leak into another format. */
object AiProfileSelector {
    fun select(
        setCode: String?,
        requested: AiProfile?,
        fallback: AiProfile = AiProfile.PRODUCTION_CANDIDATE_LANDDROP,
    ): AiProfile {
        if (requested == null) return fallback
        val restriction = requested.restrictedToSet ?: return requested
        return if (setCode?.uppercase() == restriction.uppercase()) requested else fallback
    }
}
