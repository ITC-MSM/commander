package com.wingedsheep.ai.engine.budget

import com.wingedsheep.ai.engine.lifePoolsOf
import com.wingedsheep.ai.engine.sidesFor
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId

/**
 * Decides how much thought one decision is worth.
 *
 * Lives on `AiProfile`, so a profile is what an arena run is actually measuring: `just arena
 * v0-budget-100 v0-budget-3000` is two agents differing in nothing but this.
 */
interface BudgetPolicy {
    /**
     * The budget for a priority window.
     *
     * @param meaningfulActions the candidate list *after* the meaningful-action filter, or the raw
     *   affordable candidates when the filter is off. Empty means the AI is about to pass.
     */
    fun budgetFor(state: GameState, playerId: EntityId, meaningfulActions: List<LegalAction>): DecisionBudget

    /**
     * The budget for answering a [com.wingedsheep.engine.core.PendingDecision].
     *
     * Separate from [budgetFor] because a decision has no candidate list to size a tier from, and
     * because it is never skippable: the engine is blocked mid-resolution until it is answered, so
     * there is no [BudgetTier.TRIVIAL] equivalent.
     */
    fun budgetForDecision(state: GameState, playerId: EntityId): DecisionBudget
}

/**
 * No tiering: every decision gets today's constants and no global deadline.
 *
 * This is what `AiProfile.LEGACY_V0` runs, and it must stay bit-identical to the pre-Phase-4 AI —
 * `FrozenBaselineTest` hashes V0's action stream precisely so that a refactor cannot quietly move
 * the permanent reference opponent.
 */
object LegacyBudgetPolicy : BudgetPolicy {
    override fun budgetFor(
        state: GameState,
        playerId: EntityId,
        meaningfulActions: List<LegalAction>,
    ): DecisionBudget = DecisionBudget.legacy()

    override fun budgetForDecision(state: GameState, playerId: EntityId): DecisionBudget =
        DecisionBudget.legacy()

    override fun toString(): String = "legacy"
}

/**
 * The four-tier policy from `backlog/engine-ai-improvement.md` §4b.
 *
 * | Tier | When |
 * |---|---|
 * | TRIVIAL | Nothing meaningful to choose between — the AI is passing regardless |
 * | ROUTINE | An opponent's-turn window with no immediate threat; upkeep, draw, end step |
 * | NORMAL | Our main phase, or a response to something on the stack |
 * | CRITICAL | Combat declaration, or either side within one swing of lethal |
 *
 * Two entries from the plan's table are **not** implemented and are deliberately not faked:
 * "sweeper castable or on the stack" and "a real counterspell window" both need to know what a card
 * *does*, which is Phase 6's `CardIntent`. Guessing them from a mana cost would put the most
 * expensive tier on the wrong windows, which is worse than leaving them at NORMAL.
 *
 * @param normalMillis size of the [BudgetTier.NORMAL] tier. Every other tier scales off it in the
 *   published ratio (1:10 routine, 5:2 critical), which is what makes `ArenaBudgetScalingTest`'s
 *   "the same agent at 100 / 1000 / 3000 ms" a single-parameter sweep.
 */
class TieredBudgetPolicy(
    val normalMillis: Long = BudgetTier.NORMAL.millis,
) : BudgetPolicy {

    override fun budgetFor(
        state: GameState,
        playerId: EntityId,
        meaningfulActions: List<LegalAction>,
    ): DecisionBudget {
        val tier = tierFor(state, playerId, meaningfulActions)
        val millis = millisFor(tier)
        return DecisionBudget(tier, SearchAllowances.forMillis(millis), millis)
    }

    override fun budgetForDecision(state: GameState, playerId: EntityId): DecisionBudget {
        val tier = if (someoneIsInLethalRange(state, playerId)) BudgetTier.CRITICAL else BudgetTier.NORMAL
        val millis = millisFor(tier)
        return DecisionBudget(tier, SearchAllowances.forMillis(millis), millis)
    }

    fun millisFor(tier: BudgetTier): Long = when (tier) {
        BudgetTier.TRIVIAL -> 0
        BudgetTier.ROUTINE -> normalMillis / 10
        BudgetTier.NORMAL -> normalMillis
        BudgetTier.CRITICAL -> normalMillis * 5 / 2
    }

    fun tierFor(
        state: GameState,
        playerId: EntityId,
        meaningfulActions: List<LegalAction>,
    ): BudgetTier {
        if (meaningfulActions.isEmpty()) return BudgetTier.TRIVIAL

        // Combat declaration is always CRITICAL, so combat never gets *less* time than it has
        // today — the risk register's "combat's 1 s cap fights the global budget" line.
        if (meaningfulActions.any {
                it.actionType == "DeclareAttackers" || it.actionType == "DeclareBlockers"
            }
        ) {
            return BudgetTier.CRITICAL
        }

        if (someoneIsInLethalRange(state, playerId)) return BudgetTier.CRITICAL

        // Something is on the stack and we are choosing whether to answer it.
        if (state.stack.isNotEmpty()) return BudgetTier.NORMAL

        val isMyTurn = state.isActiveTurnFor(playerId)
        if (isMyTurn && (state.step == Step.PRECOMBAT_MAIN || state.step == Step.POSTCOMBAT_MAIN)) {
            return BudgetTier.NORMAL
        }

        return BudgetTier.ROUTINE
    }

    override fun toString(): String = "tiered(${normalMillis}ms)"

    /**
     * Is either side one combat step away from dying?
     *
     * A deliberately crude proxy — the total power of a side's untapped creatures against the
     * smallest life pool facing it — because it runs at every priority window. It over-triggers
     * (blockers exist; not every creature can attack) and that is the safe direction: the cost of a
     * false CRITICAL is one slow decision, the cost of a false ROUTINE is a missed lethal.
     */
    private fun someoneIsInLethalRange(state: GameState, playerId: EntityId): Boolean {
        val sides = state.sidesFor(playerId) ?: return false
        val myLife = state.lifePoolsOf(sides.mine).minOrNull() ?: return false
        val myPower = untappedPowerOf(state, sides.mine)
        return sides.opponents.any { opponent ->
            val theirLife = state.lifePoolsOf(opponent).minOrNull() ?: return@any false
            val theirPower = untappedPowerOf(state, opponent)
            theirPower >= myLife || myPower >= theirLife
        }
    }

    private fun untappedPowerOf(state: GameState, side: List<EntityId>): Int {
        val projected = state.projectedState
        return side.sumOf { player ->
            projected.getBattlefieldControlledBy(player).sumOf { entityId ->
                if (!projected.isCreature(entityId)) return@sumOf 0
                if (state.getEntity(entityId)?.get<TappedComponent>() != null) return@sumOf 0
                (projected.getPower(entityId) ?: 0).coerceAtLeast(0)
            }
        }
    }
}
