package com.wingedsheep.gym

import com.wingedsheep.gym.contract.ActionParams
import com.wingedsheep.gym.contract.ObservationResult

/**
 * A self-contained gym environment the service layer can drive uniformly.
 *
 * Two implementations exist:
 * - [GameGymEnv] — a game of Magic (wraps [GameEnvironment]).
 * - [com.wingedsheep.gym.deckbuild.DeckbuildEnvironment] — turning a sealed pool into a deck.
 *
 * Each env owns its own action bookkeeping: `observe` produces the observation an agent
 * acts on, and `step` resolves an action ID *from the most recent observation* and advances.
 * Game-specific operations (decision submission, snapshot/restore, reset) live on
 * [GameGymEnv] only; the service casts when it needs them.
 */
interface GymEnv {

    /** True once the env reached a terminal state (game over, or deck finalized). */
    val isTerminal: Boolean

    /**
     * Current observation without advancing. [revealAll] is honoured by game envs
     * (unmask opponent hand/libraries) and ignored by deckbuild envs, which have no
     * hidden information. Passing null uses the env's configured default.
     */
    fun observe(revealAll: Boolean? = null): ObservationResult

    /**
     * Advance by the action with [actionId] from the most recent observation.
     *
     * [params] completes an action the enumerator could only offer as a template — which creatures
     * attack and whom, which blocks are made, a spell's targets, X. Omit them (or pass
     * [ActionParams.EMPTY]) for an action that needs no choice beyond its ID; a deckbuild env has
     * no such actions and rejects any non-empty params.
     *
     * @throws IllegalArgumentException if the ID is stale / not valid this step, if [params] carry
     *   a field the action can't use, or if the completed action is illegal.
     */
    fun step(actionId: Int, params: ActionParams = ActionParams.EMPTY): ObservationResult

    /** Branch this env. Children diverge independently from the next [step] on. */
    fun fork(): GymEnv
}
