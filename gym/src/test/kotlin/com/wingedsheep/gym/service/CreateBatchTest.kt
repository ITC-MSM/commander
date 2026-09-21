package com.wingedsheep.gym.service

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.contract.TrainingObservation
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class CreateBatchTest : FunSpec({
    fun registry(): CardRegistry = CardRegistry().apply {
        register(PortalSet.cards)
        register(PortalSet.basicLands)
    }

    fun config() = EnvConfig(
        players = listOf(
            PlayerSpec(
                name = "Alice",
                deck = DeckSpec.Explicit(mapOf("Mountain" to 17, "Raging Goblin" to 3)),
                playerId = EntityId("alice")
            ),
            PlayerSpec(
                name = "Bob",
                deck = DeckSpec.Explicit(mapOf("Mountain" to 17, "Raging Goblin" to 3)),
                playerId = EntityId("bob")
            )
        ),
        skipMulligans = true,
        startingPlayerIndex = 0,
        perspectivePlayerIndex = 0
    )

    test("createBatch creates independent envs in request order") {
        val svc = MultiEnvService(registry(), workerPool = EnvWorkerPool(parallelism = 2))

        val created = svc.createBatch(listOf(config(), config().copy(perspectivePlayerIndex = 1)))

        created shouldHaveSize 2
        created.map { (it.observation.observation as TrainingObservation).perspectivePlayerId } shouldBe
            listOf(EntityId("alice"), EntityId("bob"))
        created.map { it.envId }.toSet().size shouldBe 2
        svc.listEnvs() shouldBe created.map { it.envId }.toSet()
        created.forEach { result ->
            svc.observe(result.envId).observation.stateDigest shouldBe
                result.observation.observation.stateDigest
        }
    }

    test("createBatch disposes successful siblings when one create fails") {
        val svc = MultiEnvService(registry(), workerPool = EnvWorkerPool(parallelism = 2))
        val existing = svc.create(config()).envId
        val bad = config().copy(
            players = config().players.mapIndexed { index, player ->
                if (index == 0) player.copy(deck = DeckSpec.RandomSealed(setCode = "POR")) else player
            }
        )

        val error = shouldThrow<IllegalArgumentException> {
            svc.createBatch(listOf(config(), bad, config()))
        }

        error.message shouldContain "create batch item index=1 failed"
        error.message shouldContain "RandomSealed requires a BoosterGenerator"
        svc.listEnvs() shouldBe setOf(existing)
    }

    test("interrupted createBatch disposes environments completed after its caller returns") {
        val pool = EnvWorkerPool(parallelism = 2)
        val svc = MultiEnvService(registry(), workerPool = pool)
        val started = CountDownLatch(2)
        val release = CountDownLatch(1)
        val failure = AtomicReference<Throwable?>()
        val blockers = thread {
            pool.invokeAll(List(2) {
                Callable {
                    started.countDown()
                    release.await()
                }
            })
        }
        try {
            started.await(5, TimeUnit.SECONDS) shouldBe true
            val caller = thread {
                Thread.currentThread().interrupt()
                try {
                    svc.createBatch(listOf(config(), config()))
                } catch (error: Throwable) {
                    failure.set(error)
                } finally {
                    Thread.interrupted()
                }
            }
            caller.join(5000)
            caller.isAlive shouldBe false
            (failure.get() is InterruptedException) shouldBe true
        } finally {
            release.countDown()
            blockers.join(5000)
            pool.close(awaitSeconds = 30)
        }
        svc.listEnvs() shouldBe emptySet()
    }

    test("createBatch accepts an empty request") {
        val svc = MultiEnvService(registry())

        svc.createBatch(emptyList()) shouldBe emptyList()
        svc.listEnvs() shouldBe emptySet()
    }
})
