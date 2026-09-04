package com.wingedsheep.gameserver.session

import com.wingedsheep.gameserver.ScenarioTestBase
import com.wingedsheep.gameserver.ai.AiReplayHistory
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.socket.WebSocketSession

/**
 * [GameSession.getAiRuntimeSnapshot] against the two session shapes that differ in what they can
 * offer an AI: a recorded game, and an injected one that records nothing.
 *
 * The distinction that matters is that "no replay inputs" is not "no position". A dev-scenario or
 * hotseat session has a perfectly good live state and no way to reach it from an input stream, and
 * a controller reading that session should get the state and be told the history is missing —
 * withholding both is what the [AiReplayHistory.Unavailable] case exists to prevent.
 */
class AiRuntimeSnapshotTest : ScenarioTestBase() {

    private fun mockWs(id: String): WebSocketSession =
        mockk(relaxed = true) { every { this@mockk.id } returns id }

    private fun recordedGame(): GameSession {
        val session = GameSession(cardRegistry = cardRegistry, maxPlayers = 2)
        val p1 = com.wingedsheep.sdk.model.EntityId.of("snap-p1")
        val p2 = com.wingedsheep.sdk.model.EntityId.of("snap-p2")
        session.addPlayer(PlayerSession(mockWs("snap-ws1"), p1, "Alice"), mapOf("Forest" to 40))
        session.addPlayer(PlayerSession(mockWs("snap-ws2"), p2, "Bob"), mapOf("Forest" to 40))
        session.startGame()
        return session
    }

    init {
        test("a session that has not started offers no snapshot at all") {
            val session = GameSession(cardRegistry = cardRegistry, maxPlayers = 2)

            session.getAiRuntimeSnapshot().shouldBeNull()
        }

        test("a recorded game reports its state with a complete history") {
            val session = recordedGame()

            val snapshot = session.getAiRuntimeSnapshot().shouldNotBeNull()
            snapshot.state shouldBe session.getStateSnapshot()
            val history = snapshot.replayHistory.shouldBeInstanceOf<AiReplayHistory.Complete>()
            history.setup shouldBe session.getReplaySetup()
            history.actions shouldBe session.getRecordedActions()
            history.yields shouldBe session.getReplayYields()
        }

        test("an injected session still reports its live state, with the history typed as missing") {
            val injected = GameSession(cardRegistry = cardRegistry, maxPlayers = 2)
            injected.injectStateForDevScenario(recordedGame().getStateSnapshot()!!)

            val snapshot = injected.getAiRuntimeSnapshot().shouldNotBeNull()
            snapshot.state shouldBe injected.getStateSnapshot()
            snapshot.replayHistory shouldBe AiReplayHistory.Unavailable
            // The flush path still declines it — an unrecorded session has nothing to store.
            injected.replayRecordingSnapshot().shouldBeNull()
        }
    }
}
