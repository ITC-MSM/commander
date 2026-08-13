package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.core.CardsRevealedEvent
import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.core.ExileFromTopRepeatingContinuation
import com.wingedsheep.engine.core.GameEvent as EngineGameEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.handlers.effects.DamageUtils
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ZoneMovementUtils
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.ExileFromTopRepeatingEffect
import kotlin.reflect.KClass

/**
 * Executor for Demonlord Belzenlok-style repeated exile instructions.
 *
 * The matching card's move to hand is replacement-aware. In particular, a
 * commander may be sent to the command zone instead (CR 903.9b), in which case
 * [ExileFromTopRepeatingContinuation] preserves the loop's progress while the
 * player makes that choice. The aggregate damage is applied only after the final
 * iteration, never once per pause.
 */
class ExileFromTopRepeatingExecutor : EffectExecutor<ExileFromTopRepeatingEffect> {

    override val effectType: KClass<ExileFromTopRepeatingEffect> = ExileFromTopRepeatingEffect::class
    private val predicateEvaluator = PredicateEvaluator()

    override fun execute(
        state: GameState,
        effect: ExileFromTopRepeatingEffect,
        context: EffectContext
    ): EffectResult = executeFromIteration(state, effect, context, cardsPutIntoHand = 0)

    /** Resume after the generic zone-change chain performed the matching card's move. */
    fun resumeAfterHandMove(
        state: GameState,
        continuation: ExileFromTopRepeatingContinuation
    ): EffectResult {
        val owner = state.getEntity(continuation.matchingCardId)
            ?.get<CardComponent>()?.ownerId ?: continuation.effectContext.controllerId
        val reachedHand = continuation.matchingCardId in state.getZone(ZoneKey(owner, Zone.HAND))
        val cardsPutIntoHand = continuation.cardsPutIntoHandBefore + if (reachedHand) 1 else 0

        // "If the card's mana value …" uses the matched card's captured value. A
        // replacement can change zones but does not make the already revealed hit
        // disappear from this instruction.
        return if (continuation.matchingCardManaValue >= continuation.effect.repeatIfManaValueAtLeast) {
            executeFromIteration(state, continuation.effect, continuation.effectContext, cardsPutIntoHand)
        } else {
            finish(state, continuation.effect, continuation.effectContext, cardsPutIntoHand)
        }
    }

    private fun executeFromIteration(
        state: GameState,
        effect: ExileFromTopRepeatingEffect,
        context: EffectContext,
        cardsPutIntoHand: Int
    ): EffectResult {
        val controllerId = context.controllerId
        val sourceId = context.sourceId
        val predicateContext = PredicateContext.fromEffectContext(context)
        var currentState = state
        val allEvents = mutableListOf<EngineGameEvent>()
        var currentCardsToHand = cardsPutIntoHand
        var continueProcess = true

        while (continueProcess) {
            val library = currentState.getZone(ZoneKey(controllerId, Zone.LIBRARY))
            if (library.isEmpty()) break

            val allExiled = mutableListOf<EntityId>()
            var matchCard: EntityId? = null
            for (cardId in library) {
                allExiled.add(cardId)
                if (predicateEvaluator.matches(currentState, currentState.projectedState, cardId, effect.matchFilter, predicateContext)) {
                    matchCard = cardId
                    break
                }
            }

            if (allExiled.isNotEmpty()) {
                allEvents.add(CardsRevealedEvent(
                    revealingPlayerId = controllerId,
                    cardIds = allExiled.toList(),
                    cardNames = allExiled.map { currentState.getEntity(it)?.get<CardComponent>()?.name ?: "Unknown" },
                    imageUris = allExiled.map { currentState.getEntity(it)?.get<CardComponent>()?.imageUri },
                    source = sourceId?.let { currentState.getEntity(it)?.get<CardComponent>()?.name }
                ))
            }

            val cardsToExile = if (matchCard != null) allExiled.filter { it != matchCard } else allExiled
            for (cardId in cardsToExile) {
                val exileResult = ZoneMovementUtils.moveCardToZone(currentState, cardId, Zone.EXILE)
                if (exileResult.isSuccess) {
                    currentState = exileResult.state
                    allEvents.addAll(exileResult.events)
                }
            }

            if (matchCard == null) {
                continueProcess = false
                continue
            }

            val matchManaValue = currentState.getEntity(matchCard)?.get<CardComponent>()?.manaValue ?: 0
            // Pre-push the tail: an optional Commander replacement frame is then
            // placed above it, so it can perform exactly one move before this loop
            // resumes. If no decision is needed, remove the unused tail immediately.
            val withRemainder = currentState.pushContinuation(
                ExileFromTopRepeatingContinuation(
                    effect = effect,
                    effectContext = context,
                    cardsPutIntoHandBefore = currentCardsToHand,
                    matchingCardId = matchCard,
                    matchingCardManaValue = matchManaValue
                )
            )
            val handAttempt = ZoneTransitionService.attemptMoveToZone(withRemainder, matchCard, Zone.HAND)
            if (handAttempt.isPaused) {
                return EffectResult.paused(handAttempt.state, handAttempt.pendingDecision!!, allEvents + handAttempt.events)
            }

            val (_, withoutRemainder) = handAttempt.state.popContinuation()
            currentState = withoutRemainder
            allEvents.addAll(handAttempt.events)
            currentCardsToHand++
            continueProcess = matchManaValue >= effect.repeatIfManaValueAtLeast
        }

        return finish(currentState, effect, context, currentCardsToHand, allEvents)
    }

    private fun finish(
        state: GameState,
        effect: ExileFromTopRepeatingEffect,
        context: EffectContext,
        cardsPutIntoHand: Int,
        priorEvents: List<EngineGameEvent> = emptyList()
    ): EffectResult {
        var currentState = state
        val allEvents = priorEvents.toMutableList()
        if (cardsPutIntoHand > 0 && effect.damagePerCard > 0) {
            val result = DamageUtils.dealDamageToTarget(
                currentState, context.controllerId, cardsPutIntoHand * effect.damagePerCard, context.sourceId
            )
            currentState = result.state
            allEvents.addAll(result.events)
        }
        return EffectResult.success(currentState, allEvents)
    }
}
