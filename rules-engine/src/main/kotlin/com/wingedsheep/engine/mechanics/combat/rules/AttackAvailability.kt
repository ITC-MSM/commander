package com.wingedsheep.engine.mechanics.combat.rules

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.sdk.model.EntityId

/**
 * "Could this creature be declared as an attacker right now?" — the per-creature half of the
 * declare-attackers legality check (CR 508.1a), asked outside the declare-attackers step.
 *
 * It runs the very same [defaultAttackRestrictionRules] list that [
 * com.wingedsheep.engine.mechanics.combat.AttackPhaseManager] enforces when attackers are actually
 * declared, so the two can never drift: if this says a creature can attack, declaring it would pass
 * the per-creature gate. It deliberately does *not* consider the per-defender rules
 * ([defaultAttackDefenderRules]) — those need a chosen defender, which doesn't exist yet.
 *
 * The caller is a cost enumerator: tapping a creature to pay Crew N / Saddle N / Teamwork N takes it
 * out of combat, because an attacker must be untapped. Surfacing this on each candidate lets the
 * client spend the creatures that weren't going to attack anyway.
 */
object AttackAvailability {

    private val rules: List<AttackRestrictionRule> = defaultAttackRestrictionRules()

    /**
     * True iff [entityId] passes every per-creature attack restriction for [playerId] in [state].
     * [projected] must be the projection the caller is already working with — attack legality reads
     * projected types, keywords and controller, so base state would miss animated lands, granted
     * haste, and control changes.
     */
    fun canAttack(
        state: GameState,
        projected: ProjectedState,
        entityId: EntityId,
        playerId: EntityId,
        cardRegistry: CardRegistry
    ): Boolean {
        val ctx = AttackCheckContext(
            state = state,
            projected = projected,
            attackerId = entityId,
            attackingPlayer = playerId,
            cardRegistry = cardRegistry
        )
        return rules.all { it.check(ctx) == null }
    }
}
