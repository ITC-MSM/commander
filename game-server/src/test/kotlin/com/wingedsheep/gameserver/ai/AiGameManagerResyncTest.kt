package com.wingedsheep.gameserver.ai

import com.wingedsheep.ai.AiPlayerController
import com.wingedsheep.ai.engine.SealedDeckGenerator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gameserver.config.AiProperties
import com.wingedsheep.gameserver.config.GameProperties
import com.wingedsheep.gameserver.session.GameSession
import com.wingedsheep.gameserver.session.PlayerIdentity
import com.wingedsheep.gameserver.session.SessionRegistry
import com.wingedsheep.gameserver.tournament.llm.LlmCostTracker
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AiGameManagerResyncTest : FunSpec({
    test("rewiring an AI seat forces the next transport update to be a full state") {
        val aiPlayerId = EntityId.of("external-ai")
        val sessions = SessionRegistry()
        sessions.preRegisterIdentity(
            PlayerIdentity(
                token = "external-ai",
                playerId = aiPlayerId,
                playerName = "External AI",
                isAi = true,
            )
        )

        val game = mockk<GameSession>(relaxed = true) {
            every { sessionId } returns "game-resync"
        }
        val provider = object : AiControllerProvider {
            override val mode: String = "external-test"
            override fun create(context: AiControllerContext): AiPlayerController = mockk(relaxed = true)
        }
        val properties = GameProperties(
            ai = AiProperties(enabled = true, mode = provider.mode),
        )
        val manager = AiGameManager(
            gameProperties = properties,
            sessionRegistry = sessions,
            deckGenerator = mockk<SealedDeckGenerator>(relaxed = true),
            cardRegistry = mockk<CardRegistry>(relaxed = true),
            llmCostTracker = LlmCostTracker(),
            aiInsightService = AiInsightService(GameProperties()),
            controllerProviders = listOf(provider),
        )

        manager.wireAiForGame(
            gameSession = game,
            aiPlayerId = aiPlayerId,
            deckList = null,
            onActionReady = { _, _, _ -> },
            onMulliganKeep = { _ -> },
            onMulliganTake = { _ -> },
            onBottomCards = { _, _ -> },
        )

        verify(exactly = 1) { game.clearLastSentState(aiPlayerId) }
        sessions.destroy()
    }
})
