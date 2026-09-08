package com.wingedsheep.gameserver.lobby

import com.wingedsheep.engine.limited.BoosterGenerator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gameserver.persistence.restoreTournamentLobby
import com.wingedsheep.gameserver.persistence.toPersistent
import com.wingedsheep.gameserver.session.PlayerIdentity
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The draft "no time limit" setting. [TournamentLobby.NO_PICK_TIME_LIMIT] is a value of the
 * existing `pickTimeSeconds` field rather than a separate flag, so the thing worth pinning is
 * that every layer that touches that field treats `0` as the sentinel rather than as an
 * out-of-range duration to be clamped back up into a live timer.
 */
class DraftPickTimeLimitTest : FunSpec({
    val forest = CardDefinition.basicLand("Forest", Subtype.FOREST)
    val generator = BoosterGenerator(
        mapOf(
            "TST" to BoosterGenerator.SetConfig(
                setCode = "TST",
                setName = "Test",
                cards = emptyList(),
                basicLands = listOf(forest),
            )
        )
    )

    fun lobby(pickTimeSeconds: Int): TournamentLobby {
        val lobby = TournamentLobby(
            setCodes = listOf("TST"),
            setNames = listOf("Test"),
            boosterGenerator = generator,
            format = TournamentFormat.DRAFT,
            pickTimeSeconds = pickTimeSeconds,
        )
        lobby.addPlayer(PlayerIdentity(playerId = EntityId("host"), playerName = "Host"))
        return lobby
    }

    test("a pick time of zero means the draft runs no timer") {
        lobby(TournamentLobby.NO_PICK_TIME_LIMIT).hasPickTimer shouldBe false
    }

    test("any real duration still runs a timer") {
        lobby(45).hasPickTimer shouldBe true
        lobby(1).hasPickTimer shouldBe true
    }

    test("the clamp lets the untimed sentinel through but pins every real duration") {
        // The whole point: clamping 0 up to the 15s floor would silently re-arm the timer on a
        // draft the host deliberately made untimed.
        TournamentLobby.clampPickTimeSeconds(0, 120) shouldBe TournamentLobby.NO_PICK_TIME_LIMIT
        TournamentLobby.clampPickTimeSeconds(45, 120) shouldBe 45
        TournamentLobby.clampPickTimeSeconds(5, 120) shouldBe 15
        TournamentLobby.clampPickTimeSeconds(9999, 120) shouldBe 120
        // A negative is a malformed duration, not the sentinel — it clamps to the floor.
        TournamentLobby.clampPickTimeSeconds(-5, 120) shouldBe 15
    }

    test("the untimed setting survives a lobby restart") {
        val lobby = lobby(TournamentLobby.NO_PICK_TIME_LIMIT)
        val registry = CardRegistry().also { it.register(listOf(forest)) }

        val (restored, _) = restoreTournamentLobby(lobby.toPersistent(), registry, generator)

        restored.pickTimeSeconds shouldBe TournamentLobby.NO_PICK_TIME_LIMIT
        restored.hasPickTimer shouldBe false
    }

    test("the broadcast lobby settings carry the untimed setting") {
        lobby(TournamentLobby.NO_PICK_TIME_LIMIT)
            .buildLobbyUpdate(EntityId("host")).settings.pickTimeSeconds shouldBe
            TournamentLobby.NO_PICK_TIME_LIMIT
    }
})
