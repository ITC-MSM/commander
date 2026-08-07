package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.model.EntityId

/**
 * Consume one shield counter from [entityId], the single mutation behind both halves of
 * CR 122.1c.
 *
 * One or more shield counters on a permanent create a **single** replacement effect and a
 * **single** prevention effect:
 *
 * - "If this permanent would be destroyed as the result of an effect, instead remove a shield
 *   counter from it."
 * - "If damage would be dealt to this permanent, prevent that damage and remove a shield counter
 *   from it."
 *
 * Both consume exactly **one** counter per event no matter how many are on the permanent, which is
 * why this atom is fixed at 1 rather than parameterized: a creature with three shield counters
 * survives three separate damage or destroy events, not one event three times over.
 *
 * This is the analogue of [untapOrConsumeStun]'s stun branch (CR 122.1d) — an inherent rule of the
 * counter wired at the chokepoints that can trigger it, not an ability of the permanent. Per the
 * official rulings a creature that loses all its abilities is still protected, and shield counters
 * are *not* keyword counters (deliberately absent from `StateProjector.KEYWORD_COUNTER_MAP`).
 *
 * The two chokepoints that call this:
 * - `DamageUtils.dealDamageToTarget` — the prevention half.
 * - `ZoneMovementUtils.destroyPermanent` and `MoveCollectionExecutor`'s `MoveType.Destroy` branch —
 *   the replacement half. Deliberately *not* the lethal-damage state-based action
 *   (`LethalDamageCheck`): 122.1c replaces destruction "as the result of an **effect**", and the
 *   rulings confirm a creature with a shield counter still dies to the SBA when it has lethal
 *   damage marked on it or was dealt unpreventable damage by a deathtouch source.
 *
 * @return the updated state paired with the [CountersRemovedEvent] to emit, or `null` when
 *   [entityId] has no shield counter (so callers can fall through to the unreplaced behavior).
 */
fun consumeShieldCounter(state: GameState, entityId: EntityId): Pair<GameState, CountersRemovedEvent>? {
    val container = state.getEntity(entityId) ?: return null
    val counters = container.get<CountersComponent>() ?: return null
    if (counters.getCount(CounterType.SHIELD) <= 0) return null

    val newState = state.updateEntity(entityId) { c ->
        c.with(counters.withRemoved(CounterType.SHIELD, 1))
    }
    val event = CountersRemovedEvent(
        entityId,
        CounterType.SHIELD.name,
        1,
        container.get<CardComponent>()?.name ?: "Permanent"
    )
    return newState to event
}

/** True if [entityId] currently has at least one shield counter on it (CR 122.1c). */
fun hasShieldCounter(state: GameState, entityId: EntityId): Boolean =
    (state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.SHIELD) ?: 0) > 0

/**
 * Outcome of a shield counter meeting an incoming damage instance — see [applyShieldCounterToDamage].
 *
 * @property state the state with the counter already removed
 * @property event the [CountersRemovedEvent] the caller must emit
 * @property damagePrevented whether the damage itself is prevented; false when prevention is
 *   switched off (Leyline of Punishment, Fear, Fire, Foes!), in which case the caller keeps dealing
 *   the damage but the counter is still gone
 */
data class ShieldedDamage(
    val state: GameState,
    val event: CountersRemovedEvent,
    val damagePrevented: Boolean,
)

/**
 * The prevention half of CR 122.1c: "If damage would be dealt to this permanent, prevent that damage
 * and remove a shield counter from it."
 *
 * Single home for the rule, shared by the two damage-application paths — `DamageUtils`
 * (noncombat/effect damage) and `CombatDamageManager.applySingleAssignment` (combat damage, which
 * marks damage itself instead of routing through `DamageUtils.dealDamageToTarget`).
 *
 * Note the asymmetry the official rulings require: when damage *can't be prevented*, the damage is
 * still dealt **and** a shield counter is still removed. So the counter is consumed unconditionally
 * once one is present; only [ShieldedDamage.damagePrevented] is gated on [cantBePrevented].
 *
 * Players never carry shield counters (CR 122.1c is written for permanents), so callers may pass any
 * recipient; a player simply has no counters and gets `null`.
 *
 * @return `null` when [entityId] has no shield counter, so callers fall through to the normal
 *   damage-application path unchanged.
 */
fun applyShieldCounterToDamage(
    state: GameState,
    entityId: EntityId,
    cantBePrevented: Boolean,
): ShieldedDamage? {
    val (newState, event) = consumeShieldCounter(state, entityId) ?: return null
    return ShieldedDamage(newState, event, damagePrevented = !cantBePrevented)
}
