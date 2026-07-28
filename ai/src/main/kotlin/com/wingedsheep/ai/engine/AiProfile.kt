package com.wingedsheep.ai.engine

import com.wingedsheep.ai.engine.advisor.CardAdvisorModule
import com.wingedsheep.ai.engine.advisor.modules.BloomburrowAdvisorModule
import com.wingedsheep.ai.engine.advisor.modules.OnslaughtAdvisorModule
import com.wingedsheep.ai.engine.budget.BudgetPolicy
import com.wingedsheep.ai.engine.budget.LegacyBudgetPolicy
import com.wingedsheep.ai.engine.budget.TieredBudgetPolicy
import com.wingedsheep.ai.engine.evaluation.BoardEvaluator
import com.wingedsheep.ai.engine.evaluation.BoardPresence
import com.wingedsheep.ai.engine.evaluation.CardAdvantage
import com.wingedsheep.ai.engine.evaluation.CompositeBoardEvaluator
import com.wingedsheep.ai.engine.evaluation.LifeDifferential
import com.wingedsheep.ai.engine.evaluation.Tempo
import com.wingedsheep.ai.engine.evaluation.ThreatAssessment

/**
 * Weights for the five features [CompositeBoardEvaluator] combines.
 *
 * Every number here is a hand-guessed literal — that is the point of naming them: the arena can now
 * pit two weight sets against each other as two agents, and Phase 9 of
 * `backlog/engine-ai-improvement.md` replaces the guesses with a logistic fit.
 *
 * [DEFAULT] reproduces the values that used to be inline in `AIPlayer.defaultEvaluator()`, so
 * changing nothing changes nothing.
 */
data class EvaluationWeights(
    /** Life differential. Matters more when life is low — the non-linearity is inside the feature. */
    val life: Double = 1.0,
    /** Board presence. Creatures win games, so this is the largest term. */
    val boardPresence: Double = 1.5,
    /** Card advantage — a long-term edge. */
    val cardAdvantage: Double = 1.0,
    /** Threat assessment. Catches lethal-on-board situations the other features miss. */
    val threatAssessment: Double = 1.2,
    /** Tempo (mana development). Matters early, less late. Counts lands only today. */
    val tempo: Double = 0.6,
) {
    fun toEvaluator(): BoardEvaluator = CompositeBoardEvaluator(
        listOf(
            life to LifeDifferential,
            boardPresence to BoardPresence,
            cardAdvantage to CardAdvantage,
            threatAssessment to ThreatAssessment,
            tempo to Tempo,
        )
    )

    companion object {
        val DEFAULT = EvaluationWeights()

        /**
         * Every weight zero, so the evaluator returns a constant and the Strategist can only
         * ever pass. Not a playable agent — it exists so the arena can prove it *discriminates*:
         * a harness that cannot separate this from [AiProfile.LEGACY_V0] is measuring noise.
         */
        val BLIND = EvaluationWeights(0.0, 0.0, 0.0, 0.0, 0.0)
    }
}

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
    /** Leaf-evaluation weights. See [EvaluationWeights]. */
    val evaluationWeights: EvaluationWeights = EvaluationWeights.DEFAULT,
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
         * What a player actually faces in a real game — see [EngineAiPlayerController]. Naming it
         * here makes the production configuration arena-runnable instead of a literal buried in a
         * controller constructor.
         */
        val PRODUCTION = AiProfile(
            id = "production",
            advisorModules = listOf(BloomburrowAdvisorModule(), OnslaughtAdvisorModule()),
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
    }
}
