package com.wingedsheep.engine.mechanics.combat

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.combat.AttackersDeclaredThisCombatComponent
import com.wingedsheep.engine.state.components.combat.BlockersDeclaredThisCombatComponent
import com.wingedsheep.engine.state.components.player.CombatDeclarationControlComponent
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId

/**
 * Who makes a combat *declaration* (Master Warcraft: "You choose which creatures attack this turn.
 * You choose which creatures block this turn and how those creatures block.").
 *
 * A declaration is owed by the active player in an undeclared declare attackers step, and by each
 * defending player in an undeclared declare blockers step (CR 508.1 / 509.1). Normally the player
 * who owes it makes it; while a [CombatDeclarationControlComponent] is in force this turn, the
 * player carrying it makes it instead. The declaration itself is unchanged — it is still the owing
 * player's `DeclareAttackers` / `DeclareBlockers`, validated against their creatures and their
 * requirements and restrictions — only the *input* moves.
 *
 * Deliberately separate from [GameState.actorFor]: that one moves all input *and* the right to see
 * the controlled player's hidden zones (Mindslaver), while this moves exactly two choices and
 * nothing else.
 */
object CombatDeclarationControl {

    /** True while [playerId] owes an attack or block declaration that hasn't been made yet. */
    fun owesDeclaration(state: GameState, playerId: EntityId): Boolean {
        val entity = state.getEntity(playerId) ?: return false
        if (state.step == Step.DECLARE_ATTACKERS && state.isActiveTurnFor(playerId)) {
            return entity.get<AttackersDeclaredThisCombatComponent>() == null
        }
        if (state.step == Step.DECLARE_BLOCKERS && !state.isActiveTurnFor(playerId)) {
            return entity.get<BlockersDeclaredThisCombatComponent>() == null &&
                CombatDefenders.isDefendingPlayer(state, playerId)
        }
        return false
    }

    /** The player who makes every combat declaration this turn, or null when nobody took them over. */
    fun controllerThisTurn(state: GameState): EntityId? =
        state.turnOrder
            .mapNotNull { id ->
                state.getEntity(id)?.get<CombatDeclarationControlComponent>()
                    ?.takeIf { it.turnNumber == state.turnNumber }
                    ?.let { id to it.timestamp }
            }
            .maxByOrNull { it.second }
            ?.first
            ?.takeIf { it in state.activePlayers }

    /**
     * The player who takes over the declaration [playerId] owes right now, or null when [playerId]
     * (or whoever already acts for them) makes it as usual.
     */
    fun declarerFor(state: GameState, playerId: EntityId): EntityId? {
        if (!owesDeclaration(state, playerId)) return null
        return controllerThisTurn(state)
    }

    /**
     * Whose input the game is waiting on for [playerId]'s current window: the combat-declaration
     * controller while [playerId] owes a declaration it has taken over, otherwise the usual
     * [GameState.actorFor]. Use for *routing input* (legal actions, authorization, auto-pass) —
     * never for visibility, which stays with [GameState.actorFor].
     */
    fun inputActorFor(state: GameState, playerId: EntityId): EntityId =
        declarerFor(state, playerId) ?: state.actorFor(playerId)
}
