package com.wingedsheep.engine.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * How a piece of engine work ended. It is exactly one of three things, and callers `when` over it
 * instead of reading booleans that each answer only part of the question.
 */
@Serializable
sealed interface Outcome {

    /** The work ran to completion. */
    @Serializable
    @SerialName("Done")
    data object Done : Outcome

    /**
     * The work stopped to ask a player [decision], the question on top of the continuation stack.
     * It is in flight, not failed: the answer is an action that resumes it.
     */
    @Serializable
    @SerialName("Paused")
    data class Paused(val decision: PendingDecision) : Outcome

    /**
     * The work was refused. At the [ActionProcessor] boundary a rejected action leaves the state
     * exactly as it was.
     */
    @Serializable
    @SerialName("Rejected")
    data class Rejected(val reason: Rejection) : Outcome
}

/** Why work was refused, typed by who is at fault. */
@Serializable
sealed interface Rejection {
    val message: String

    /**
     * Validation refused the action before anything ran: it isn't legal in this state. A client
     * sent something stale or wrong. This is routine, not an engine fault.
     */
    @Serializable
    @SerialName("IllegalAction")
    data class IllegalAction(override val message: String) : Rejection

    /**
     * The action passed validation, but carrying it out failed partway. Either the validator
     * missed a legality rule that execution enforces, or an engine invariant broke. Both deserve a
     * look, which is why this is kept apart from [IllegalAction].
     */
    @Serializable
    @SerialName("ExecutionFailed")
    data class ExecutionFailed(override val message: String) : Rejection
}
