package com.wingedsheep.gameserver.ai

import com.wingedsheep.ai.AiPlayerController
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.gameserver.handler.GamePlayHandler
import com.wingedsheep.gameserver.handler.MessageSender
import com.wingedsheep.gameserver.session.GameSession
import com.wingedsheep.gameserver.session.PlayerSession
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ExternalAiRejectedActionTest : FunSpec({
    test("rejected external AI action does not enter server strategic fallback") {
        val aiPlayerId = EntityId.of("external-ai")
        val controller = mockk<AiPlayerController>(relaxed = true)
        val aiSocket = AiWebSocketSession(
            aiPlayerId = aiPlayerId,
            controller = controller,
            thinkingDelayMs = 0,
            onActionReady = { _, _, _ -> },
            onMulliganKeep = { _ -> },
            onMulliganTake = { _ -> },
            onBottomCards = { _, _ -> },
            allowActionsOnlyFallback = false,
        )
        val game = mockk<GameSession>(relaxed = true) {
            every { sessionId } returns "external-ai-rejection"
            every { getPlayerSession(aiPlayerId) } returns PlayerSession(
                webSocketSession = aiSocket,
                playerId = aiPlayerId,
                playerName = "External AI",
            )
        }
        val action = PassPriority(aiPlayerId)
        val epoch = "external-epoch"
        every { game.executeAiAction(aiPlayerId, action, epoch) } returns
            GameSession.ActionResult.Failure("authoritative rejection")

        val sender = mockk<MessageSender>(relaxed = true)
        val handler = GamePlayHandler(
            sessionRegistry = mockk(relaxed = true),
            gameRepository = mockk(relaxed = true),
            lobbyRepository = mockk(relaxed = true),
            sender = sender,
            cardRegistry = mockk(relaxed = true),
            printingRegistry = mockk(relaxed = true),
            tokenArtRegistry = mockk(relaxed = true),
            deckGenerator = mockk(relaxed = true),
            gameProperties = mockk(relaxed = true),
            replayService = mockk(relaxed = true),
            replayCheckpointFlusher = mockk(relaxed = true),
            engineVersion = mockk(relaxed = true),
            aiGameManager = mockk(relaxed = true),
            matchResultSink = mockk(relaxed = true),
            rankedResultSink = mockk(relaxed = true),
            deckProfiler = mockk(relaxed = true),
        )

        try {
            handler.handleAiAction(game, aiPlayerId, action, epoch)

            verify(exactly = 1) { game.executeAiAction(any(), any(), any()) }
            verify(exactly = 1) { game.executeAiAction(aiPlayerId, action, epoch) }
            verify(exactly = 0) { game.getLegalActions(any()) }
            verify(exactly = 0) { game.noteAiActionRejected(any(), any()) }
            verify(exactly = 0) { sender.send(any(), any()) }
        } finally {
            aiSocket.close()
        }
    }
})
