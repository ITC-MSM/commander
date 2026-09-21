package com.wingedsheep.gameserver.ai

import com.wingedsheep.ai.ActionResponse
import com.wingedsheep.ai.AiPlayerController
import com.wingedsheep.engine.core.GameAction
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.view.ClientGameState
import com.wingedsheep.engine.view.LegalActionInfo
import com.wingedsheep.engine.view.StateDelta
import com.wingedsheep.gameserver.protocol.ServerMessage
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AiWebSocketSessionFallbackTest : FunSpec({
    val player = EntityId.of("external-ai")
    val pass = PassPriority(player)
    val actions = listOf(LegalActionInfo("PassPriority", "Pass priority", pass))
    val state = ClientGameState(
        viewingPlayerId = player,
        cards = emptyMap(),
        zones = emptyList(),
        players = emptyList(),
        currentPhase = Phase.PRECOMBAT_MAIN,
        currentStep = Step.PRECOMBAT_MAIN,
        activePlayerId = player,
        priorityPlayerId = player,
        turnNumber = 1,
        isGameOver = false,
        winnerId = null,
        combat = null,
    )

    test("external delta without a baseline cannot submit a heuristic and a full update restores controller use") {
        val controller = mockk<AiPlayerController>()
        every { controller.chooseAction(any(), any(), any(), any()) } returns ActionResponse.SubmitAction(pass)
        val submitted = mutableListOf<Pair<GameAction, String?>>()
        val socket = AiWebSocketSession(
            aiPlayerId = player,
            controller = controller,
            thinkingDelayMs = 0,
            onActionReady = { _, action, epoch -> submitted.add(action to epoch) },
            onMulliganKeep = {},
            onMulliganTake = {},
            onBottomCards = { _, _ -> },
            allowActionsOnlyFallback = false,
        )
        try {
            shouldThrow<IllegalStateException> {
                socket.handleServerMessage(ServerMessage.StateDeltaUpdate(
                    delta = StateDelta(players = emptyList(), turnNumber = 2),
                    events = emptyList(),
                    legalActions = actions,
                    interactionEpoch = "missing-baseline",
                ))
            }
            submitted shouldBe emptyList()
            verify(exactly = 0) { controller.chooseAction(any(), any(), any(), any()) }

            socket.handleServerMessage(ServerMessage.StateUpdate(
                state = state,
                events = emptyList(),
                legalActions = actions,
                interactionEpoch = "full",
            ))
            socket.handleServerMessage(ServerMessage.StateDeltaUpdate(
                delta = StateDelta(players = emptyList(), turnNumber = 3),
                events = emptyList(),
                legalActions = actions,
                interactionEpoch = "delta",
            ))

            submitted shouldBe listOf(pass to "full", pass to "delta")
            verify(exactly = 1) { controller.chooseAction(state, actions, null, emptyList()) }
            verify(exactly = 1) {
                controller.chooseAction(state.copy(turnNumber = 3), actions, null, emptyList())
            }
        } finally {
            socket.close()
        }
    }

    test("built-in seats retain actions-only fallback with the original interaction epoch") {
        val controller = mockk<AiPlayerController>()
        val submitted = mutableListOf<Pair<GameAction, String?>>()
        val socket = AiWebSocketSession(
            aiPlayerId = player,
            controller = controller,
            thinkingDelayMs = 0,
            onActionReady = { _, action, epoch -> submitted.add(action to epoch) },
            onMulliganKeep = {},
            onMulliganTake = {},
            onBottomCards = { _, _ -> },
            allowActionsOnlyFallback = true,
        )
        try {
            socket.handleServerMessage(ServerMessage.StateDeltaUpdate(
                delta = StateDelta(players = emptyList()),
                events = emptyList(),
                legalActions = actions,
                interactionEpoch = "fallback-origin",
            ))

            submitted shouldBe listOf(pass to "fallback-origin")
            verify(exactly = 0) { controller.chooseAction(any(), any(), any(), any()) }
        } finally {
            socket.close()
        }
    }
})
