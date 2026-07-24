package com.wingedsheep.gameserver.session

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.model.EntityId
import org.springframework.web.socket.WebSocketSession

/**
 * The spectator set is keyed by identity, not by socket: a spectator who refreshes (or leaves and
 * comes back) reconnects on a brand-new [PlayerSession], and must still be listed exactly once in
 * the "N watching" badge the seated players see.
 */
class GameSessionSpectatorTest : FunSpec({

    fun ws(id: String, open: Boolean = true): WebSocketSession = mockk(relaxed = true) {
        every { this@mockk.id } returns id
        every { isOpen } returns open
    }

    fun session() = GameSession(cardRegistry = CardRegistry())

    val watcherId = EntityId.of("watcher")

    test("a spectator reconnecting on a new socket is listed once, on the live socket") {
        val session = session()
        val old = PlayerSession(ws("ws-old", open = false), watcherId, "Watcher")
        val new = PlayerSession(ws("ws-new"), watcherId, "Watcher")

        session.addSpectator(old)
        session.addSpectator(new)

        session.getSpectators().map { it.sessionId } shouldContainExactly listOf("ws-new")
        session.spectatorCountMessage().count shouldBe 1
        session.spectatorCountMessage().spectatorNames shouldContainExactly listOf("Watcher")
    }

    test("cleanup for a socket the spectator already replaced doesn't evict their live session") {
        val session = session()
        val old = PlayerSession(ws("ws-old", open = false), watcherId, "Watcher")
        val new = PlayerSession(ws("ws-new"), watcherId, "Watcher")

        session.addSpectator(old)
        session.addSpectator(new)
        session.removeSpectator(old)

        session.getSpectators().map { it.sessionId } shouldContainExactly listOf("ws-new")
    }

    test("stopping spectating on the current socket removes the spectator") {
        val session = session()
        val watcher = PlayerSession(ws("ws-1"), watcherId, "Watcher")

        session.addSpectator(watcher)
        session.removeSpectator(watcher)

        session.getSpectators().shouldBeEmpty()
        session.spectatorCountMessage().count shouldBe 0
    }

    test("a spectator whose socket closed is not counted, and is pruned") {
        val session = session()
        session.addSpectator(PlayerSession(ws("ws-gone", open = false), watcherId, "Watcher"))
        session.addSpectator(PlayerSession(ws("ws-live"), EntityId.of("other"), "Other"))

        session.getSpectators().map { it.playerName } shouldContainExactly listOf("Other")

        session.pruneDisconnectedSpectators().shouldBeTrue()
        session.pruneDisconnectedSpectators().shouldBeFalse()
        session.getSpectators().map { it.playerName } shouldContainExactly listOf("Other")
    }

    test("distinct spectators are all listed") {
        val session = session()
        session.addSpectator(PlayerSession(ws("ws-1"), EntityId.of("a"), "Ann"))
        session.addSpectator(PlayerSession(ws("ws-2"), EntityId.of("b"), "Bob"))

        session.spectatorCountMessage().count shouldBe 2
        session.spectatorCountMessage().spectatorNames.sorted() shouldContainExactly listOf("Ann", "Bob")
    }
})
