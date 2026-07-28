package com.wingedsheep.ai.arena

import com.wingedsheep.ai.engine.AIPlayer
import com.wingedsheep.ai.engine.AiProfile
import com.wingedsheep.ai.engine.EvaluationWeights
import com.wingedsheep.ai.engine.advisor.modules.BloomburrowAdvisorModule
import com.wingedsheep.ai.engine.advisor.modules.OnslaughtAdvisorModule
import com.wingedsheep.ai.engine.budget.TieredBudgetPolicy
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.model.EntityId

/**
 * A named competitor in the arena — an [AiProfile] plus the short name you type on the command
 * line (`just arena v0 blb-advisors 1000`).
 *
 * One [AIPlayer] is built per seat per game and never shared: `GameSimulator.isResolving` and
 * `decisionResolver` are mutable instance state, so a shared instance would corrupt its own
 * recursion guard across concurrent games.
 */
data class ArenaAgent(val name: String, val profile: AiProfile) {
    fun createPlayer(registry: CardRegistry, playerId: EntityId): AIPlayer =
        AIPlayer.create(registry, playerId, profile)
}

/**
 * The agents `just arena` can name.
 *
 * Adding an agent here is how a later phase enters the scoreboard: build the [AiProfile], give it
 * a name, and every arena/gauntlet recipe can reach it with no other wiring.
 */
object ArenaAgents {

    private val all: List<ArenaAgent> = listOf(
        // The permanent reference opponent. Every version reports against this one.
        ArenaAgent("v0", AiProfile.LEGACY_V0),
        // Whatever `AIPlayer.create(registry, playerId)` builds today.
        ArenaAgent("current", AiProfile.CURRENT),
        // What a player actually faces in a real game: BLB + ONS card advisors.
        ArenaAgent("production", AiProfile.PRODUCTION),
        // V0 plus one advisor module each — this is the split `AdvisorBenchmark` measured, so
        // `just arena v0 blb-advisors` is directly comparable to its published number.
        ArenaAgent("blb-advisors", AiProfile.LEGACY_V0.copy(
            id = "blb-advisors",
            advisorModules = listOf(BloomburrowAdvisorModule()),
        )),
        ArenaAgent("ons-advisors", AiProfile.LEGACY_V0.copy(
            id = "ons-advisors",
            advisorModules = listOf(OnslaughtAdvisorModule()),
        )),
        // Not a playable agent — every evaluation weight is zero, so the Strategist can never
        // prefer an action to passing. It exists to prove the harness *discriminates*: an arena
        // that cannot separate this from `v0` is measuring noise, not strength.
        ArenaAgent("v0-blind", AiProfile.LEGACY_V0.copy(
            id = "v0-blind",
            evaluationWeights = EvaluationWeights.BLIND,
        )),
        // ── Phase 4 ──
        // The meaningful-action filter alone. `just arena v0 v0-meaningful 1000` is the phase's
        // exit criterion: at ≥50% the filter costs nothing, and if it *loses* it is discarding a
        // real option and has found a bug for free.
        ArenaAgent("v0-meaningful", AiProfile.LEGACY_V0.copy(
            id = "v0-meaningful",
            useMeaningfulFilter = true,
        )),
        // The decision budget alone, at four sizes. `ArenaBudgetScalingTest` plays these against
        // each other: strength must be monotone in the budget, or the search is making noise.
        ArenaAgent("v0-budget-100", budgetAgent(100)),
        ArenaAgent("v0-budget-1000", budgetAgent(1_000)),
        ArenaAgent("v0-budget-2000", budgetAgent(2_000)),
        ArenaAgent("v0-budget-3000", budgetAgent(3_000)),
        // Both, at the nominal budget sizes — what Phase 4 proposes to ship.
        ArenaAgent("v0-phase4", AiProfile.PHASE4),
    )

    /** `v0` with nothing changed but the size of a [TieredBudgetPolicy]'s NORMAL tier. */
    private fun budgetAgent(normalMillis: Long): AiProfile = AiProfile.LEGACY_V0.copy(
        id = "v0-budget-$normalMillis",
        budgetPolicy = TieredBudgetPolicy(normalMillis),
    )

    private val byName: Map<String, ArenaAgent> = all.associateBy { it.name }

    val names: List<String> get() = all.map { it.name }

    fun resolve(name: String): ArenaAgent = byName[name]
        ?: throw IllegalArgumentException(
            "Unknown arena agent \"$name\". Known agents: ${names.joinToString(", ")}"
        )
}
