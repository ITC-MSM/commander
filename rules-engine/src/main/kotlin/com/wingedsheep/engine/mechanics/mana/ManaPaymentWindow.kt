package com.wingedsheep.engine.mechanics.mana

import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.ManaSourceOption
import com.wingedsheep.engine.core.ReopenManaPaymentDecisionContinuation
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.model.EntityId

/**
 * CR 605.3a — "A player may activate an activated mana ability whenever they have priority,
 * **whenever they are casting a spell or activating an ability that requires a mana payment, or
 * whenever a rule or effect asks for a mana payment**, even if it's in the middle of casting or
 * resolving a spell or activating or resolving an ability."
 *
 * A [SelectManaSourcesDecision] is exactly that third clause: the engine has stopped the game to
 * ask one player for mana (ward, "you may pay {B}", a pay-to-attack tax, a draw replacement, …).
 * While it is open, that player holds no priority, so the ordinary `priorityPlayerId` gate in
 * `ActivateAbilityHandler` would reject every mana ability — leaving the pre-computed
 * [SelectManaSourcesDecision.availableSources] menu as the only way to produce mana. That menu is
 * deliberately narrow: [ManaSolver.findAvailableManaSources] only models `{T}`-shaped abilities, so
 * anything with a discard/Forage/sacrifice-something-else sub-cost, or a cost with no `{T}` at all
 * (Ashnod's Altar), was simply unreachable during a payment.
 *
 * This object is the single definition of "a mana payment is being asked for right now". Both the
 * engine's authority check (`ActivateAbilityHandler.validate`) and the server's offer
 * (`GameSession.getLegalActions`) read it, so the two can't drift.
 *
 * Activating a mana ability inside the window must leave the window itself untouched: the mana goes
 * to the pool, the decision is re-raised (refreshed — see [refresh]), and the player pays with the
 * floating mana when they confirm. Every payment resumer already spends the pool before tapping
 * anything, so no resumer needed to change.
 */
object ManaPaymentWindow {

    /**
     * The open mana-payment decision [playerId] is being asked to pay, or `null` if the game isn't
     * currently asking them for mana.
     *
     * [actorId] is the seat submitting the action, which may be driving another player's turn
     * (Mindslaver-style control). The window belongs to the *paying* player; the actor only needs
     * to be whoever currently drives them.
     */
    fun openFor(state: GameState, actorId: EntityId): SelectManaSourcesDecision? {
        val decision = state.pendingDecision as? SelectManaSourcesDecision ?: return null
        return decision.takeIf { state.actorFor(it.playerId) == actorId }
    }

    /**
     * Sets the window aside so a mana ability can resolve against a decision-free state, and
     * queues its restoration.
     *
     * The [ReopenManaPaymentDecisionContinuation] is pushed *above* the payment continuation that
     * is already on the stack, so if the mana ability raises a decision of its own (choosing a
     * color for Birds of Paradise, a Fertile Ground tap bonus) that decision nests on top and the
     * window is re-raised only once the ability has fully resolved.
     */
    fun suspend(state: GameState, decision: SelectManaSourcesDecision): GameState =
        state.clearPendingDecision()
            .pushContinuation(ReopenManaPaymentDecisionContinuation(decision.id, decision))

    /**
     * Re-raises the window that [suspend] set aside, popping its continuation frame.
     *
     * Returns `null` when the frame isn't on top — the mana ability paused for a nested decision,
     * so the frame stays put and the auto-resumer will re-raise the window later.
     */
    fun resumeIfPending(
        state: GameState,
        events: List<GameEvent>,
        cardRegistry: CardRegistry
    ): ExecutionResult? {
        val frame = state.peekContinuation() as? ReopenManaPaymentDecisionContinuation ?: return null
        val (_, popped) = state.popContinuation()
        return reopen(popped, frame.decision, events, cardRegistry)
    }

    /** Re-raises [decision], refreshed against the post-activation board. */
    fun reopen(
        state: GameState,
        decision: SelectManaSourcesDecision,
        events: List<GameEvent>,
        cardRegistry: CardRegistry
    ): ExecutionResult {
        val refreshed = refresh(state, decision, cardRegistry)
        return ExecutionResult.paused(state.withPendingDecision(refreshed), refreshed, events)
    }

    /**
     * Recomputes the menu against the current board so a source the player just tapped by hand
     * stops being offered, and the auto-pay suggestion covers only what the floating mana doesn't.
     *
     * The refreshed [SelectManaSourcesDecision.availableSources] is always a subset of the original
     * — activating a mana ability consumes sources, it never creates them — which matters because
     * the payment continuation still validates the player's final submission against the list it
     * captured when the window first opened.
     */
    fun refresh(
        state: GameState,
        decision: SelectManaSourcesDecision,
        cardRegistry: CardRegistry
    ): SelectManaSourcesDecision {
        val solver = ManaSolver(cardRegistry)
        val stillAvailable = solver.findAvailableManaSources(state, decision.playerId)
            .map { source ->
                ManaSourceOption(
                    entityId = source.entityId,
                    name = source.name,
                    producesColors = source.producesColors,
                    producesColorless = source.producesColorless,
                    requiresSacrifice = source.requiresSacrifice,
                    requiresTappingAnotherPermanent = source.tapPermanentsSubCost != null
                )
            }
            .associateBy { it.entityId }

        // Keep the original entries (the continuation validates against those) but drop the ones
        // the board no longer offers.
        val availableSources = decision.availableSources.filter { it.entityId in stillAvailable }

        val remaining = remainingCost(state, decision)
        val autoPaySuggestion = when {
            remaining == null || remaining.isEmpty() -> emptyList()
            else -> solver.solve(state, decision.playerId, remaining)?.sources?.map { it.entityId }
                ?: emptyList()
        }

        return decision.copy(
            availableSources = availableSources,
            autoPaySuggestion = autoPaySuggestion.filter { id -> availableSources.any { it.entityId == id } }
        )
    }

    /**
     * Whether [playerId]'s floating mana already covers [cost] in full.
     *
     * Payment resumers use this to tell "I refuse to pay" apart from "I already floated the mana
     * myself" — both submit an empty source selection.
     */
    fun floatingManaCovers(
        state: GameState,
        playerId: EntityId,
        cost: com.wingedsheep.sdk.core.ManaCost
    ): Boolean {
        val pool = state.getEntity(playerId)
            ?.get<com.wingedsheep.engine.state.components.player.ManaPoolComponent>()
            ?: return false
        return ManaPool(pool.white, pool.blue, pool.black, pool.red, pool.green, pool.colorless)
            .payPartial(cost)
            .remainingCost
            .isEmpty()
    }

    /** [SelectManaSourcesDecision.requiredCost] minus what the player already has floating. */
    private fun remainingCost(
        state: GameState,
        decision: SelectManaSourcesDecision
    ): com.wingedsheep.sdk.core.ManaCost? {
        val cost = runCatching { com.wingedsheep.sdk.core.ManaCost.parse(decision.requiredCost) }
            .getOrNull() ?: return null
        val pool = state.getEntity(decision.playerId)
            ?.get<com.wingedsheep.engine.state.components.player.ManaPoolComponent>()
            ?: return cost
        return ManaPool(pool.white, pool.blue, pool.black, pool.red, pool.green, pool.colorless)
            .payPartial(cost)
            .remainingCost
    }
}
