package com.wingedsheep.gym.contract

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.core.GameAction
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.model.EntityId
import kotlinx.serialization.Serializable

/**
 * The choices an action needs that its ID alone can't carry.
 *
 * An action ID resolves to the [com.wingedsheep.engine.legalactions.LegalAction] the enumerator
 * produced, and for several action types that `GameAction` is a *template*, not a complete move:
 * `DeclareAttackers` is enumerated with an empty attacker map (the enumerator advertises the
 * candidates in `validAttackers` / `validAttackTargets` and leaves the choice to the player), and
 * the same is true of `DeclareBlockers`, of a spell's targets, and of X. Stepping such an action
 * with nothing attached is a legal move — it just means "attack with nobody", "block with nobody",
 * "no targets" — which is why the gap was silent: combat was unreachable over the HTTP API while
 * every request returned 200.
 *
 * These params are how a caller completes the template. They are validated by the engine like any
 * other action: an illegal attacker assignment is rejected rather than quietly dropped (see
 * [com.wingedsheep.gym.GameGymEnv.step]).
 *
 * Not expressible here, deliberately — each has its own channel:
 * - Complex decisions (target-selection pauses, damage assignment, ordering, …) → `POST
 *   /envs/{id}/decision` with a typed `DecisionResponse`.
 * - Attacking bands (CR 702.22), alternative/additional cost payments, convoke/delve/improvise
 *   selections. A step carrying params for an action that can't use them is rejected with a
 *   message naming the action, never ignored.
 *
 * @property attackers attacker entity id → the player, planeswalker or battle it attacks.
 * @property blockers blocker entity id → the attackers it blocks, in order.
 * @property targets Targets for a cast/activation, in target-requirement order. Ids are resolved
 *   against the current state: a player id becomes a player target, an object on the stack a spell
 *   target, a battlefield permanent a permanent target, and a card in any other zone a card target.
 * @property xValue The value chosen for X.
 */
@Serializable
data class ActionParams(
    val attackers: Map<EntityId, EntityId> = emptyMap(),
    val blockers: Map<EntityId, List<EntityId>> = emptyMap(),
    val targets: List<EntityId> = emptyList(),
    val xValue: Int? = null
) {
    val isEmpty: Boolean
        get() = populatedFields.isEmpty()

    /** The names of the fields actually carrying a choice — the vocabulary of the error messages. */
    internal val populatedFields: List<String>
        get() = buildList {
            if (attackers.isNotEmpty()) add("attackers")
            if (blockers.isNotEmpty()) add("blockers")
            if (targets.isNotEmpty()) add("targets")
            if (xValue != null) add("xValue")
        }

    companion object {
        val EMPTY = ActionParams()
    }
}

/**
 * Folds [ActionParams] into the template `GameAction` an action ID resolved to.
 *
 * Pure — it builds the action the engine will then validate; it does not check legality itself.
 * Anything it cannot express is an [IllegalArgumentException] (→ HTTP 400) rather than a silently
 * dropped choice, which is the failure mode this whole type exists to remove.
 */
object ActionParameterizer {

    fun apply(action: GameAction, params: ActionParams, state: GameState): GameAction {
        if (params.isEmpty) return action

        return when (action) {
            is DeclareAttackers -> {
                params.allowOnly(action, "attackers")
                action.copy(attackers = params.attackers)
            }

            is DeclareBlockers -> {
                params.allowOnly(action, "blockers")
                action.copy(blockers = params.blockers)
            }

            is CastSpell -> {
                params.allowOnly(action, "targets", "xValue")
                action.copy(
                    targets = params.targets.map { resolveTarget(it, state) }
                        .ifEmpty { action.targets },
                    xValue = params.xValue ?: action.xValue
                )
            }

            is ActivateAbility -> {
                params.allowOnly(action, "targets", "xValue")
                action.copy(
                    targets = params.targets.map { resolveTarget(it, state) }
                        .ifEmpty { action.targets },
                    xValue = params.xValue ?: action.xValue
                )
            }

            else -> throw IllegalArgumentException(
                "Action ${action::class.simpleName} takes no step params; got $params"
            )
        }
    }

    /**
     * Turn a bare entity id into the [ChosenTarget] variant its current zone implies. The caller
     * sends ids because that is all the observation exposes; which variant an id means is a fact
     * about the game state, not about the request.
     *
     * `internal` rather than private so the zone dispatch can be asserted directly — it is the only
     * part of this object that reads live state, and driving a game to each of the four zones just
     * to reach it would test the driver, not the dispatch.
     */
    internal fun resolveTarget(id: EntityId, state: GameState): ChosenTarget = when {
        id in state.turnOrder -> ChosenTarget.Player(id)
        id in state.stack -> ChosenTarget.Spell(id)
        id in state.getBattlefield() -> ChosenTarget.Permanent(id)
        else -> {
            val key = state.zones.entries.firstOrNull { (_, ids) -> id in ids }?.key
                ?: throw IllegalArgumentException("Target $id is in no zone of the current state")
            ChosenTarget.Card(cardId = id, ownerId = key.ownerId, zone = key.zoneType)
        }
    }

    /**
     * Reject any populated field [action] can't use. Stated as a whitelist rather than as one
     * rejection per inapplicable field, so a new [ActionParams] field is inapplicable everywhere by
     * default instead of being silently dropped by every branch that forgot to name it.
     */
    private fun ActionParams.allowOnly(action: GameAction, vararg allowed: String) {
        val unusable = populatedFields - allowed.toSet()
        require(unusable.isEmpty()) {
            "Step param(s) ${unusable.joinToString(", ") { "'$it'" }} " +
                "are not applicable to ${action::class.simpleName}"
        }
    }
}
