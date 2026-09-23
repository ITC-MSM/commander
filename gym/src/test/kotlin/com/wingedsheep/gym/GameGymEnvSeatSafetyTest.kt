package com.wingedsheep.gym

import com.wingedsheep.engine.core.MayAbilityContinuation
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.suspendForDecision
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.core.ActionParams
import com.wingedsheep.gym.contract.TrainingObservation
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class GameGymEnvSeatSafetyTest : FunSpec({
    fun registry(): CardRegistry = CardRegistry().apply {
        register(PortalSet.cards)
        register(PortalSet.basicLands)
    }

    test("non-acting seat cannot observe or submit the acting seat's decision surface") {
        val environment = GameEnvironment.create(registry())
        environment.reset(
            GameConfig(
                players = listOf(
                    PlayerConfig("Alice", Deck.of("Mountain" to 20)),
                    PlayerConfig("Bob", Deck.of("Mountain" to 20))
                ),
                skipMulligans = true,
                startingPlayerIndex = 0,
                seed = 20260920L
            )
        )

        val alice = environment.playerIds[0]
        val bob = environment.playerIds[1]
        val suspended = environment.state.suspendForDecision(
            question = { id ->
                YesNoDecision(
                    id = id,
                    playerId = bob,
                    prompt = "Secret decision for Bob",
                    context = DecisionContext(sourceId = EntityId("hidden-source"))
                )
            },
            answer = MayAbilityContinuation(
                playerId = bob,
                sourceName = null,
                effectIfYes = null,
                effectIfNo = null,
                effectContext = EffectContext(sourceId = null, controllerId = bob)
            )
        )
        environment.restore(suspended.state, environment.playerIds)

        val gymEnv = GameGymEnv(
            environment = environment,
            perspectivePlayerIndex = 0,
            defaultRevealAll = false
        )

        val aliceView = gymEnv.observe().observation as TrainingObservation
        aliceView.perspectivePlayerId shouldBe alice
        aliceView.agentToAct shouldBe bob
        aliceView.pendingDecision shouldBe null
        aliceView.legalActions.shouldBeEmpty()
        shouldThrow<IllegalArgumentException> {
            gymEnv.step(0, ActionParams())
        }

        val beforeRejectedDecision = environment.state
        shouldThrow<IllegalArgumentException> {
            gymEnv.submitDecision(YesNoResponse(suspended.state.pendingDecision!!.id, false))
        }
        environment.state shouldBe beforeRejectedDecision

        val bobView = gymEnv.observeForPlayer(bob).observation as TrainingObservation
        bobView.perspectivePlayerId shouldBe bob
        bobView.agentToAct shouldBe bob
        bobView.pendingDecision.shouldNotBeNull()
        bobView.legalActions.shouldNotBeEmpty()

        shouldThrow<IllegalArgumentException> {
            gymEnv.observeForPlayer(EntityId("not-seated"))
        }

        // Observing the other seat must revoke both folded and structured decisions.
        gymEnv.observeForPlayer(alice)
        shouldThrow<IllegalArgumentException> {
            gymEnv.submitDecision(YesNoResponse(suspended.state.pendingDecision!!.id, false))
        }
        shouldThrow<IllegalArgumentException> { gymEnv.step(0, ActionParams()) }

        val debugView = gymEnv.observe(revealAll = true).observation as TrainingObservation
        debugView.pendingDecision.shouldNotBeNull()
        debugView.legalActions.shouldNotBeEmpty()

        gymEnv.observeForPlayer(bob)
        gymEnv.submitDecision(YesNoResponse(suspended.state.pendingDecision!!.id, false))
        environment.state.pendingDecision shouldBe null
    }
})
