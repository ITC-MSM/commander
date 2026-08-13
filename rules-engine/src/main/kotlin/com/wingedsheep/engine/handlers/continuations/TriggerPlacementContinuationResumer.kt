package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.*
import com.wingedsheep.engine.state.GameState

/** Resumes the explicit per-controller CR 603.3b triggered-ability ordering decision. */
class TriggerPlacementContinuationResumer(
    private val services: EngineServices,
) : ContinuationResumerModule {
    override fun resumers(): List<ContinuationResumer<*>> = listOf(
        resumer(TriggerPlacementWaveContinuation::class, ::resumeOrder),
    )

    private fun resumeOrder(
        state: GameState,
        continuation: TriggerPlacementWaveContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore,
    ): ExecutionResult {
        if (response !is TriggeredAbilitiesOrderedResponse) {
            return ExecutionResult.error(state, "Expected triggered ability order response")
        }
        val result = services.triggerProcessor.resumePlacementOrder(
            state, continuation, response.orderedAbilityIds,
        )
        return if (result.isSuccess && !result.isPaused) checkForMore(result.newState, result.events) else result
    }
}
