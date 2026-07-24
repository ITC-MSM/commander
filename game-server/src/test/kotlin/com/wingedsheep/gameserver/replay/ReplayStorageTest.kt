package com.wingedsheep.gameserver.replay

import com.wingedsheep.gameserver.ScenarioTestBase
import com.wingedsheep.gameserver.session.GameSession
import com.wingedsheep.gameserver.session.PlayerSession
import com.wingedsheep.sdk.core.AttackMode
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.socket.WebSocketSession
import java.time.Instant

/**
 * Replays have one home, and an interrupted recording resumes from it — or honestly refuses to.
 *
 * The recording used to ride along in the Redis session blob, which was written on every state
 * change and so was always current. The store is flushed on a timer instead, which is cheaper but
 * means the stored log can trail the live game. The dangerous failure isn't losing those actions —
 * it's appending the *rest* of the game onto the short prefix, producing a replay of a game nobody
 * played that looks entirely well-formed. These tests pin the fingerprint gate that prevents it.
 */
class ReplayStorageTest : ScenarioTestBase() {

    private fun mockWs(id: String): WebSocketSession =
        mockk(relaxed = true) { every { this@mockk.id } returns id }

    private fun replay(gameId: String, players: List<Pair<String, String>>, endedAt: String) =
        CompactReplay(
            gameId = gameId,
            players = players.map { (id, name) -> ReplayPlayerInfo(id, name) },
            startedAt = endedAt,
            endedAt = endedAt,
            winnerName = null,
            setup = ReplaySetup(
                seed = 1L,
                format = Format.Standard,
                attackMode = AttackMode.MULTIPLE,
                players = players.map { (id, name) ->
                    ReplayPlayerSetup(playerId = id, name = name, deck = Deck(cards = listOf("Forest")))
                },
                seatRoster = emptyList(),
            ),
            actions = emptyList(),
        )

    /** Play a real recorded game and stop after [actions] recorded actions. */
    private fun playPartialGame(): GameSession {
        val session = GameSession(cardRegistry = cardRegistry, maxPlayers = 2)
        val p1 = EntityId.of("resume-p1")
        val p2 = EntityId.of("resume-p2")
        session.addPlayer(PlayerSession(mockWs("resume-ws1"), p1, "Alice"), mapOf("Forest" to 40))
        session.addPlayer(PlayerSession(mockWs("resume-ws2"), p2, "Bob"), mapOf("Forest" to 40))
        session.startGame()
        session.keepHand(p1)
        session.keepHand(p2)
        repeat(30) {
            val state = session.getStateForTesting() ?: return@repeat
            if (state.gameOver) return@repeat
            state.priorityPlayerId?.let { session.executeAutoPass(it) }
        }
        return session
    }

    private fun GameSession.snapshotRecording() = CompactReplay(
        gameId = sessionId,
        players = getPlayers().map { ReplayPlayerInfo(it.playerId.value, it.playerName) },
        startedAt = (replayStartedAt ?: Instant.now()).toString(),
        endedAt = "",
        winnerName = null,
        setup = getReplaySetup().shouldNotBeNull(),
        actions = getRecordedActions(),
        yields = getReplayYields(),
        checkpoints = getReplayCheckpoints(),
    )

    init {
        test("the in-memory store lists a player's finished games, newest first") {
            val store = InMemoryReplayStore()
            store.save(StoredReplay(replay("g1", listOf("alice" to "Alice", "bob" to "Bob"), "2026-01-01T00:00:00Z"), ReplayStatus.FINISHED))
            store.save(StoredReplay(replay("g2", listOf("alice" to "Alice", "carol" to "Carol"), "2026-01-03T00:00:00Z"), ReplayStatus.FINISHED))
            store.save(StoredReplay(replay("g3", listOf("bob" to "Bob", "carol" to "Carol"), "2026-01-02T00:00:00Z"), ReplayStatus.FINISHED))

            store.findRecentForPlayer("alice", 10).map { it.gameId } shouldContainExactly listOf("g2", "g1")
            store.find("g3").shouldNotBeNull().replay.gameId shouldBe "g3"
            store.find("nope") shouldBe null
        }

        test("an in-progress recording is listed for resume but never in a player's history") {
            val store = InMemoryReplayStore()
            store.save(
                StoredReplay(
                    replay("live", listOf("alice" to "Alice", "bob" to "Bob"), ""),
                    ReplayStatus.IN_PROGRESS,
                    resumeFingerprint = "abc",
                )
            )

            store.findRecentForPlayer("alice", 10) shouldBe emptyList()
            store.findInProgress().map { it.replay.gameId } shouldContainExactly listOf("live")
            store.findInProgress().single().resumeFingerprint shouldBe "abc"
        }

        test("an abandoned recording is promoted to a finished, watchable replay") {
            val store = InMemoryReplayStore()
            store.save(
                StoredReplay(
                    replay("g-abandoned", listOf("alice" to "Alice", "bob" to "Bob"), ""),
                    ReplayStatus.IN_PROGRESS,
                    resumeFingerprint = "abc",
                )
            )
            val service = ReplayService(store, mockk(relaxed = true), mockk(relaxed = true))

            service.finalizePartial("g-abandoned")

            store.find("g-abandoned").shouldNotBeNull().status shouldBe ReplayStatus.FINISHED
            store.findRecentForPlayer("alice", 10).map { it.gameId } shouldContainExactly listOf("g-abandoned")
            // Finalizing twice is harmless — a finished record is left alone.
            service.finalizePartial("g-abandoned")
            store.find("g-abandoned").shouldNotBeNull().status shouldBe ReplayStatus.FINISHED
        }

        test("a recording whose flush captured the live position resumes") {
            val session = playPartialGame()
            val flushed = session.snapshotRecording()
            val fingerprint = session.getReplayResumeFingerprint().shouldNotBeNull()
            val actionCount = flushed.actions.size

            // A restart: same recovered state, recording handed back from the store.
            val resumed = session.restoreReplayRecording(flushed, fingerprint)

            resumed shouldBe true
            session.getReplaySetup().shouldNotBeNull()
            session.getRecordedActions().size shouldBe actionCount
        }

        test("a recording that trails the recovered state stops rather than splicing onto a stale prefix") {
            val session = playPartialGame()
            val stale = session.snapshotRecording()

            // The flush landed, then a few more actions were played before the crash — so the
            // fingerprint stored with the flush describes an earlier position than the recovered one.
            val resumed = session.restoreReplayRecording(stale, expectedFingerprint = "0000000000000000")

            resumed shouldBe false
            // Recording is off, so nothing further is appended and the stored prefix stays honest.
            session.getReplaySetup() shouldBe null
            session.getReplayFrameCount() shouldBe 0
        }
    }
}
