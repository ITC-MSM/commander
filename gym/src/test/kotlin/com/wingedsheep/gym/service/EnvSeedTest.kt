package com.wingedsheep.gym.service

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.shouldBe

class EnvSeedTest : FunSpec({
    val registry = CardRegistry().apply {
        register(PortalSet.cards)
        register(PortalSet.basicLands)
    }
    val deck = DeckSpec.Explicit(mapOf("Mountain" to 17, "Raging Goblin" to 3))
    val config = EnvConfig(
        players = listOf(
            PlayerSpec("Alice", deck, playerId = EntityId("alice")),
            PlayerSpec("Bob", deck, playerId = EntityId("bob"))
        ),
        skipMulligans = true,
        startingPlayerIndex = 0,
        seed = 20260920L
    )
    val service = MultiEnvService(registry)

    test("explicit EnvConfig seed reproduces the opening observation") {
        val first = service.create(config).observation.observation
        val second = service.create(config).observation.observation

        second.stateDigest shouldBe first.stateDigest
    }

    test("seed reproduces generated player IDs and random starting player") {
        val defaults = config.copy(
            players = config.players.map { it.copy(playerId = null) },
            startingPlayerIndex = null
        )
        val first = service.create(defaults).observation.observation
        val second = service.create(defaults).observation.observation

        second.stateDigest shouldBe first.stateDigest
    }

    test("reset uses the supplied seed and reproduces the full opening libraries") {
        val created = service.create(config)
        val opening = service.snapshotCodec.load(service.snapshot(created.envId)).state
        val changed = config.copy(seed = 42L)

        service.reset(created.envId, changed)
        val changedState = service.snapshotCodec.load(service.snapshot(created.envId)).state
        changedState.rng shouldNotBe opening.rng
        changedState.zones shouldNotBe opening.zones
        val independentlyCreated = service.create(changed)
        changedState.zones shouldBe service.snapshotCodec.load(service.snapshot(independentlyCreated.envId)).state.zones

        service.reset(created.envId, config).observation.stateDigest shouldBe created.observation.observation.stateDigest
        val resetState = service.snapshotCodec.load(service.snapshot(created.envId)).state
        resetState.rng shouldBe opening.rng
        resetState.zones shouldBe opening.zones
    }

    test("omitting the seed uses fresh entropy on create and reset") {
        val unseeded = EnvConfig(players = config.players, startingPlayerIndex = 0)
        val first = service.create(unseeded)
        val second = service.create(unseeded)
        val firstRng = service.snapshotCodec.load(service.snapshot(first.envId)).state.rng
        val secondRng = service.snapshotCodec.load(service.snapshot(second.envId)).state.rng

        secondRng shouldNotBe firstRng
        service.reset(first.envId, unseeded)
        service.snapshotCodec.load(service.snapshot(first.envId)).state.rng shouldNotBe firstRng
    }
})
