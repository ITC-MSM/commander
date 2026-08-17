package com.wingedsheep.engine.handlers.effects.player

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ReplacementEffectUtils
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.player.LoseAtEndStepComponent
import com.wingedsheep.engine.state.components.player.SkipNextTurnComponent
import com.wingedsheep.sdk.scripting.effects.TakeExtraTurnEffect
import kotlin.reflect.KClass

/**
 * Executor for TakeExtraTurnEffect.
 * "Take an extra turn after this one."
 *
 * Implemented by making every other player skip their next turn. In a two-player game that inserts
 * exactly one extra turn for the taker. **In a three-or-more-player game it does not:**
 * `TurnManager.endTurn` consumes at most one pending skip per turn boundary (a single `if`, not a
 * loop), so the second skipped opponent takes their turn anyway with a skip still pending. This is
 * a pre-existing limitation of the skip-based model, not of the riders below.
 *
 * If loseAtEndStep is true (e.g., Last Chance), the caster will also lose the game
 * at the beginning of their next end step.
 *
 * If powerUpAbilitiesCantBeActivated is true (Kang the Conqueror), `turnNumber + 1` is recorded in
 * [GameState.powerUpRestrictedTurns], locking every player out of power-up abilities for that turn.
 * Two caveats on which turn that actually is:
 *  - **Player count.** `turnNumber` counts turns that actually begin and a skipped turn never calls
 *    `TurnManager.startTurn`, so in a two-player game `turnNumber + 1` is the extra turn. With three
 *    or more players the skip consumption above means it can instead name an opponent's ordinary
 *    turn, locking them out on a turn that was never Kang's extra one.
 *  - **Ordering (CR 500.7).** The engine keeps no extra-turn queue, so `turnNumber + 1` is only ever
 *    "the next turn to begin". CR 500.7 gives the most recently created turn first, so if a second
 *    extra-turn effect resolves *after* this one in the same turn, that turn goes first and the
 *    lockout lands on the wrong one. Reachable with printed cards (Time Walk, Time Warp, Temporal
 *    Mastery, Karn's Temporal Sundering, …), though only when Kang resolves first; both turns
 *    usually belong to the same player, so the practical effect is a lockout on the wrong one of
 *    two consecutive turns.
 *
 * Checks for PreventExtraTurns replacement effects (e.g., Ugin's Nexus) before applying. Both riders
 * sit behind that check: the engine models Ugin's Nexus as preventing the extra turn outright, so
 * there is no "that turn" for them to apply to. Keeping the riders here rather than as sibling
 * effects in a `Composite` is what keeps "did a turn actually get created" in a single owner — a
 * sibling would have to re-derive this executor's preconditions, and would silently drift the moment
 * `TakeExtraTurn` gains another way to fail.
 */
class TakeExtraTurnExecutor : EffectExecutor<TakeExtraTurnEffect> {

    override val effectType: KClass<TakeExtraTurnEffect> = TakeExtraTurnEffect::class

    override fun execute(
        state: GameState,
        effect: TakeExtraTurnEffect,
        context: EffectContext
    ): EffectResult {
        // Resolve who takes the extra turn — defaults to the controller
        val turnTakerId = context.resolveTarget(effect.target, state)
            ?: context.controllerId

        // Check if extra turns are prevented (e.g., Ugin's Nexus on the battlefield)
        if (ReplacementEffectUtils.isExtraTurnPrevented(state)) {
            return EffectResult.success(state)
        }

        // "Take an extra turn" is modeled as every other player skipping their next turn,
        // which inserts one extra turn for the taker regardless of player count.
        val otherPlayerIds = state.getOpponents(turnTakerId)
        if (otherPlayerIds.isEmpty()) {
            return EffectResult.error(state, "No opponent found")
        }
        var newState = otherPlayerIds.fold(state) { acc, otherPlayerId ->
            acc.updateEntity(otherPlayerId) { container ->
                val existing = container.get<SkipNextTurnComponent>()?.turns ?: 0
                container.with(SkipNextTurnComponent(existing + 1))
            }
        }

        // "During that turn, power-up abilities can't be activated" (Kang the Conqueror). Stamp the
        // next turn to actually begin: skipped turns never call `TurnManager.startTurn`, so they
        // consume no turn numbers. See the KDoc for the two cases where that is not the extra turn
        // — three-or-more-player pods, and a second extra-turn effect resolving afterwards.
        if (effect.powerUpAbilitiesCantBeActivated) {
            newState = newState.copy(
                powerUpRestrictedTurns = newState.powerUpRestrictedTurns + (newState.turnNumber + 1)
            )
        }

        // If loseAtEndStep is true, mark the turn-taker to lose at their next end step
        // turnsUntilLoss=1 means skip this turn's end step, trigger on the next turn's end step
        if (effect.loseAtEndStep) {
            newState = newState.updateEntity(turnTakerId) { container ->
                container.with(
                    LoseAtEndStepComponent(
                        turnsUntilLoss = 1,
                        message = "You lose the game (Last Chance)"
                    )
                )
            }
        }

        return EffectResult.success(newState)
    }
}
