package com.wingedsheep.gym.service

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SnapshotDisposalTest : FunSpec({
    fun registry(): CardRegistry = CardRegistry().apply {
        register(PortalSet.cards)
        register(PortalSet.basicLands)
    }

    fun deck() = DeckSpec.Explicit(mapOf("Mountain" to 17, "Raging Goblin" to 3))

    test("disposed snapshot releases its slot and can no longer be restored") {
        val service = MultiEnvService(registry())
        val config = EnvConfig(
            players = listOf(
                PlayerSpec("Alice", deck()),
                PlayerSpec("Bob", deck())
            ),
            skipMulligans = true,
            startingPlayerIndex = 0
        )
        val envId = service.create(config).envId
        val handle = service.snapshot(envId)

        service.snapshotCodec.size() shouldBe 1
        service.disposeSnapshot(handle)
        service.snapshotCodec.size() shouldBe 0

        shouldThrow<NoSuchElementException> {
            service.restore(envId, handle)
        }

        service.disposeSnapshot(handle)
        service.snapshotCodec.size() shouldBe 0
    }
})
