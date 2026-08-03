package com.wingedsheep.engine.handlers.effects

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ContinuationFrame
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.DecisionPhase
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.KeywordGrantedEvent
import com.wingedsheep.engine.core.OptionMetadata
import com.wingedsheep.engine.core.PendingDecision
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.ReplacementEffectSourceComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EntersWithRiot
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Riot (CR 702.136) as a permanent enters: "You may have this permanent enter with an additional
 * +1/+1 counter on it. If you don't, it gains haste."
 *
 * Riot is deliberately *not* modelled as a recorded choice read back by gated riders. Each instance
 * works separately (CR 702.136b), so a permanent with two instances makes two independent choices
 * and can end up with a counter **and** haste — which a single-valued chosen-mode slot cannot
 * express. Instead each instance is answered by its own decision and applied the moment it is
 * answered, and this object owns the whole walk.
 *
 * Two entry shapes share it, matching the rest of the as-enters machinery:
 *  - the **spell** path, where the entering object is still the resolving spell
 *    ([com.wingedsheep.engine.core.RiotEntrySpellContinuation]), and
 *  - the **direct** path, where the permanent is already on the battlefield — reanimation, a
 *    tutored put-onto-battlefield, a minted token
 *    ([com.wingedsheep.engine.core.RiotEntryOnBattlefieldContinuation]).
 *
 * Both apply riot to the same entity id, so [applyCounter] / [applyHaste] are shared; only the
 * "what happens once the last instance resolves" tail differs, and that lives in the resumers.
 */
object RiotEntry {

    private val stateProjector = StateProjector()

    /** Option index 0 of a riot decision. */
    const val COUNTER_OPTION_INDEX: Int = 0

    private const val COUNTER_LABEL = "A +1/+1 counter"
    private const val HASTE_LABEL = "Haste"

    /**
     * The outcome of starting or continuing a riot walk.
     *
     * [decision] non-null means the caller must pause with it (the continuation is already pushed
     * onto [state]); null means riot is fully resolved and the caller carries on with [state] and
     * [events].
     */
    data class Resolution(
        val state: GameState,
        val events: List<GameEvent>,
        val decision: PendingDecision?,
    )

    /**
     * How many instances of riot apply to [enteringEntityId] as it enters (CR 702.136b) — its own
     * printed riot from [cardDef], plus every `otherOnly` grant on the battlefield whose
     * `appliesTo` filter matches it ("Other Spiders you control have riot").
     *
     * The entering object may still be a spell on the stack; the filter is evaluated against it
     * either way, which is what CR 614.12 asks for (the replacement looks at the permanent it is
     * about to become).
     */
    fun instanceCount(
        state: GameState,
        enteringEntityId: EntityId,
        cardDef: CardDefinition?,
    ): Int {
        var count = cardDef?.script?.replacementEffects
            ?.count { it is EntersWithRiot && !it.otherOnly }
            ?: 0

        for (sourceId in state.getBattlefield()) {
            if (sourceId == enteringEntityId) continue
            val container = state.getEntity(sourceId) ?: continue
            val replacements = container.get<ReplacementEffectSourceComponent>() ?: continue
            val sourceControllerId = container.get<ControllerComponent>()?.playerId ?: continue
            for (effect in replacements.replacementEffects) {
                if (effect !is EntersWithRiot || !effect.otherOnly) continue
                if (!EntersWithReplacements.matchesEnterFilter(
                        effect.appliesTo, enteringEntityId, sourceId, sourceControllerId, state
                    )
                ) continue
                count++
            }
        }
        return count
    }

    /** Convenience overload that looks the entering card's definition up in [cardRegistry]. */
    fun instanceCount(
        state: GameState,
        enteringEntityId: EntityId,
        cardRegistry: CardRegistry,
    ): Int {
        val cardComponent = state.getEntity(enteringEntityId)?.get<CardComponent>() ?: return 0
        return instanceCount(state, enteringEntityId, cardRegistry.getCard(cardComponent.cardDefinitionId))
    }

    /**
     * Can this entering permanent take riot's +1/+1 counter branch?
     *
     * A permanent that can't have +1/+1 counters put on it (Solemnity and friends) can't be
     * *chosen* to enter with one, so riot gives it haste with no decision at all — the printed riot
     * rulings say so explicitly.
     *
     * The prohibition is a projected flag over battlefield permanents, and an entering permanent
     * isn't in that projection yet on the spell path, so this probes a throwaway state with the
     * entering object placed on the battlefield. The probe is only built when riot actually
     * applies, so the extra projection is per riot creature, not per entry.
     */
    fun canTakeCounterBranch(
        state: GameState,
        enteringEntityId: EntityId,
        controllerId: EntityId,
    ): Boolean {
        val probe = if (enteringEntityId in state.getBattlefield()) {
            state
        } else {
            state
                .removeFromStack(enteringEntityId)
                .addToZone(ZoneKey(controllerId, Zone.BATTLEFIELD), enteringEntityId)
                .updateEntity(enteringEntityId) { container ->
                    if (container.get<ControllerComponent>() != null) container
                    else container.with(ControllerComponent(controllerId))
                }
        }
        return stateProjector.project(probe).canReceiveCounters(enteringEntityId)
    }

    /**
     * Start riot for [enteringEntityId], given it owes [instances] choices.
     *
     * When the counter branch is unavailable every instance is forced to haste, so they are all
     * applied here and no decision is raised. Otherwise the first instance's decision is built,
     * [continuationFactory] is asked for the frame that will resume it (the spell and direct paths
     * supply different frames), and the pushed state is returned for the caller to pause on.
     */
    fun begin(
        state: GameState,
        enteringEntityId: EntityId,
        controllerId: EntityId,
        instances: Int,
        continuationFactory: (decisionId: String, remainingInstances: Int) -> ContinuationFrame,
    ): Resolution {
        if (instances <= 0) return Resolution(state, emptyList(), null)

        if (!canTakeCounterBranch(state, enteringEntityId, controllerId)) {
            // Every instance collapses to "it gains haste" — granting haste twice is the same as
            // once, so one grant covers the lot.
            val (newState, events) = applyHaste(state, enteringEntityId, controllerId)
            return Resolution(newState, events, null)
        }

        return ask(state, enteringEntityId, controllerId, instances, continuationFactory)
    }

    /**
     * Apply the answered instance ([choseCounter]) and chain to the next one, if any.
     *
     * Shared by both resumers: the branch application and the "is there another instance" walk are
     * identical, only the tail after the final instance differs.
     */
    fun applyAndContinue(
        state: GameState,
        enteringEntityId: EntityId,
        controllerId: EntityId,
        choseCounter: Boolean,
        remainingInstances: Int,
        continuationFactory: (decisionId: String, remainingInstances: Int) -> ContinuationFrame,
    ): Resolution {
        val (afterChoice, events) = if (choseCounter) {
            applyCounter(state, enteringEntityId, controllerId)
        } else {
            applyHaste(state, enteringEntityId, controllerId)
        }

        if (remainingInstances <= 0) return Resolution(afterChoice, events, null)

        val next = ask(afterChoice, enteringEntityId, controllerId, remainingInstances, continuationFactory)
        return next.copy(events = events + next.events)
    }

    /**
     * Which of [enteredEntityIds] owe riot choices. Callers on the direct-entry paths use this to
     * decide whether to engage [walkDirectEntries] at all, and — when they do — which entities'
     * entry [com.wingedsheep.engine.core.ZoneChangeEvent]s to withhold so the riot walk can release
     * them after the counter has landed.
     */
    fun entriesOwingRiot(
        state: GameState,
        enteredEntityIds: List<EntityId>,
        cardRegistry: CardRegistry,
    ): List<EntityId> = enteredEntityIds.filter { instanceCount(state, it, cardRegistry) > 0 }

    /**
     * Walk a batch of permanents that entered the battlefield directly, resolving each one's riot
     * instances and releasing its withheld entry event once they are done. Pauses on the first
     * instance that needs a decision, carrying the rest of the batch in the continuation.
     *
     * Shared by the direct-entry executors (reanimation, tutored put-onto-battlefield, token
     * minting) and by the resumer that continues the walk, so the two can't drift.
     */
    fun walkDirectEntries(
        state: GameState,
        entities: List<EntityId>,
        cardRegistry: CardRegistry,
        fromZone: Zone?,
    ): Resolution {
        var newState = state
        val events = mutableListOf<GameEvent>()
        var queue = entities

        while (queue.isNotEmpty()) {
            val entityId = queue.first()
            val rest = queue.drop(1)
            queue = rest

            val controllerId = newState.getEntity(entityId)?.get<ControllerComponent>()?.playerId
            if (controllerId == null) {
                events += entryEvent(newState, entityId, fromZone)
                continue
            }

            val instances = instanceCount(newState, entityId, cardRegistry)
            val resolution = begin(newState, entityId, controllerId, instances) { decisionId, remaining ->
                com.wingedsheep.engine.core.RiotEntryOnBattlefieldContinuation(
                    decisionId = decisionId,
                    entityId = entityId,
                    controllerId = controllerId,
                    remainingInstances = remaining,
                    remainingEntities = rest,
                    fromZone = fromZone,
                )
            }
            newState = resolution.state
            events += resolution.events

            if (resolution.decision != null) {
                return Resolution(newState, events, resolution.decision)
            }
            events += entryEvent(newState, entityId, fromZone)
        }

        return Resolution(newState, events, null)
    }

    /**
     * The entry [com.wingedsheep.engine.core.ZoneChangeEvent] a direct-entry caller withheld while
     * riot was outstanding, re-emitted now that the permanent's counter (if any) is on it.
     */
    fun entryEvent(
        state: GameState,
        entityId: EntityId,
        fromZone: Zone?,
    ): GameEvent {
        val container = state.getEntity(entityId)
        val cardComponent = container?.get<CardComponent>()
        return com.wingedsheep.engine.core.ZoneChangeEvent(
            entityId,
            cardComponent?.name ?: "Unknown",
            fromZone,
            Zone.BATTLEFIELD,
            cardComponent?.ownerId
                ?: container?.get<ControllerComponent>()?.playerId
                ?: entityId
        )
    }

    /** Build the decision for one riot instance and push its continuation. */
    private fun ask(
        state: GameState,
        enteringEntityId: EntityId,
        controllerId: EntityId,
        instances: Int,
        continuationFactory: (decisionId: String, remainingInstances: Int) -> ContinuationFrame,
    ): Resolution {
        val name = state.getEntity(enteringEntityId)?.get<CardComponent>()?.name ?: "This creature"
        // Instance ids keep the decision id unique when a permanent owes several choices, so two
        // riot decisions in a row are never mistaken for a resubmission of the same one.
        val decisionId = "riot-${enteringEntityId.value}-$instances"
        val decision = ChooseOptionDecision(
            id = decisionId,
            playerId = controllerId,
            prompt = if (instances > 1) {
                "Riot — $name enters with your choice of a +1/+1 counter or haste ($instances choices left)"
            } else {
                "Riot — $name enters with your choice of a +1/+1 counter or haste"
            },
            context = DecisionContext(
                sourceId = enteringEntityId,
                sourceName = name,
                phase = DecisionPhase.RESOLUTION,
            ),
            options = listOf(COUNTER_LABEL, HASTE_LABEL),
            optionMetadata = listOf(
                OptionMetadata(description = "It enters with an additional +1/+1 counter on it."),
                OptionMetadata(description = "It gains haste."),
            ),
        )

        val pushed = state
            .pushContinuation(continuationFactory(decisionId, instances - 1))
            .withPendingDecision(decision)
        return Resolution(pushed, emptyList(), decision)
    }

    /**
     * Riot's counter branch: one +1/+1 counter placed as the permanent enters (CR 614.1c), through
     * the shared entry-counter path so counter-placement modifiers (Hardened Scales, Doubling
     * Season) apply exactly as they do to any other "enters with a +1/+1 counter".
     */
    fun applyCounter(
        state: GameState,
        enteringEntityId: EntityId,
        controllerId: EntityId,
    ): Pair<GameState, List<GameEvent>> {
        val name = state.getEntity(enteringEntityId)?.get<CardComponent>()?.name ?: ""
        return EntersWithReplacements.placeEntryCounters(
            state, enteringEntityId, CounterTypeFilter.PlusOnePlusOne, 1, controllerId, name
        )
    }

    /**
     * Riot's haste branch. Granted as a permanent, entry-timestamped Layer-6 floating effect —
     * the same shape `EntersWithKeywords` uses — so it survives the turn (CR 702.136a grants it
     * indefinitely), is removed by a later "loses all abilities", and is cleaned up when the
     * permanent leaves the battlefield.
     */
    fun applyHaste(
        state: GameState,
        enteringEntityId: EntityId,
        controllerId: EntityId,
    ): Pair<GameState, List<GameEvent>> {
        val name = state.getEntity(enteringEntityId)?.get<CardComponent>()?.name ?: ""
        val newState = state.addFloatingEffect(
            layer = Layer.ABILITY,
            modification = SerializableModification.GrantKeyword(Keyword.HASTE.name),
            affectedEntities = setOf(enteringEntityId),
            duration = Duration.Permanent,
            context = EffectContext(sourceId = enteringEntityId, controllerId = controllerId),
        )
        return newState to listOf(
            KeywordGrantedEvent(
                targetId = enteringEntityId,
                targetName = name,
                keyword = "haste",
                sourceName = "Riot",
            )
        )
    }
}
