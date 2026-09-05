package com.wingedsheep.ai.engine

import com.wingedsheep.ai.engine.advisor.AdvisorDecisionContext
import com.wingedsheep.ai.engine.advisor.CardAdvisor
import com.wingedsheep.ai.engine.advisor.CardAdvisorModule
import com.wingedsheep.ai.engine.advisor.CardAdvisorRegistry
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.NumberChosenResponse
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.core.SubmitDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class AIPlayerFactoryTest : FunSpec({
    fun game() = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun field(value: Any, name: String): Any? = value.javaClass.getDeclaredField(name).apply {
        isAccessible = true
    }.get(value)

    test("fresh players match standalone choices across profiles and repeated calls") {
        val driver = game()
        val state = driver.state
        val actor = driver.activePlayer!!
        val factory = AIPlayer.Factory(driver.cardRegistry)
        for (profile in listOf(AiProfile.CURRENT, AiProfile.PRODUCTION, AiProfile.PRODUCTION_CANDIDATE)) {
            val expected = AIPlayer.create(driver.cardRegistry, actor, profile).chooseAction(state)
            repeat(3) {
                factory.create(actor, profile).chooseAction(state) shouldBe expected
            }
        }
        driver.state shouldBe state
    }

    test("factory shares services while keeping strategy memory and resolver state per player") {
        val driver = game()
        val actor = driver.activePlayer!!
        val factory = AIPlayer.Factory(driver.cardRegistry)
        val first = factory.create(actor, AiProfile.PRODUCTION)
        first.chooseAction(driver.state) shouldNotBe PassPriority(actor)
        val second = factory.create(actor, AiProfile.PRODUCTION)
        val opponent = factory.create(driver.player2, AiProfile.PRODUCTION)
        // These ownership assertions target the hazard that result equality on a single root
        // misses: reusing an AI carries its loop memory and resolver into another hypothesis.
        val firstStrategy = field(first, "strategist")!!
        val secondStrategy = field(second, "strategist")!!
        (field(firstStrategy, "positionsActedFrom") as Collection<*>).isEmpty() shouldBe false
        (field(secondStrategy, "positionsActedFrom") as Collection<*>).isEmpty() shouldBe true
        field(first, "responder") shouldNotBeSameInstanceAs field(second, "responder")
        val firstSimulator = field(first, "simulator")!!
        val secondSimulator = field(second, "simulator")!!
        firstSimulator shouldNotBeSameInstanceAs secondSimulator
        secondSimulator shouldNotBeSameInstanceAs field(opponent, "simulator")
        field(firstSimulator, "processor") shouldBe field(secondSimulator, "processor")
        field(firstSimulator, "enumerator") shouldBe field(secondSimulator, "enumerator")
    }

    test("factory simulators retain their own responder callbacks through pending choices") {
        val driver = game()
        val spell = card("Factory Number Choice") {
            manaCost = "{0}"
            typeLine = "Sorcery"
            spell {
                effect = Effects.ChooseNumberThen(
                    then = Effects.GainLife(1), minValue = 1, maxValue = 3,
                )
            }
        }
        driver.registerCards(listOf(spell))
        val actor = driver.activePlayer!!
        val id = driver.putCardInHand(actor, spell.name)
        val beforeCast = driver.state
        val factory = AIPlayer.Factory(driver.cardRegistry)
        val responses = mutableListOf<Int>()
        // Distinct advisors make a callback bound to the wrong responder observable.
        fun profile(number: Int) = AiProfile.PRODUCTION.copy(
            id = "factory-number-$number",
            advisorModules = listOf(object : CardAdvisorModule {
                override fun register(registry: CardAdvisorRegistry) {
                    registry.register(object : CardAdvisor {
                        override val cardNames = setOf(spell.name)

                        override fun respondToDecision(context: AdvisorDecisionContext): NumberChosenResponse {
                            val decision = context.decision.shouldBeInstanceOf<ChooseNumberDecision>()
                            context.playerId shouldBe actor
                            responses += number
                            return NumberChosenResponse(decision.id, number)
                        }
                    })
                }
            }),
        )

        val first = factory.create(actor, profile(1))
        val firstSimulator = field(first, "simulator") as GameSimulator
        val cast = firstSimulator.getLegalActions(beforeCast, actor).single { it.action is CastSpell }.action
        fun resolve(simulator: GameSimulator) {
            // Simulate the cast directly: choosing a sole pass on a live stack skips simulation.
            val result = simulator.simulate(beforeCast, cast).shouldBeInstanceOf<SimulationResult.Terminal>()
            result.state.pendingDecision.shouldBeNull()
            result.state.stack.shouldBeEmpty()
            result.state.lifeTotal(actor) shouldBe beforeCast.lifeTotal(actor) + 1
        }

        resolve(firstSimulator)
        responses shouldBe listOf(1)
        val second = factory.create(driver.player2, profile(3))
        val secondSimulator = field(second, "simulator") as GameSimulator
        resolve(secondSimulator)
        responses shouldBe listOf(1, 3)
        resolve(firstSimulator)
        responses shouldBe listOf(1, 3, 1)
        driver.state shouldBe beforeCast

        driver.castSpell(actor, id).isSuccess shouldBe true
        driver.bothPass()
        val decision = driver.state.pendingDecision!!
        val state = driver.state
        val expected = AIPlayer.create(driver.cardRegistry, decision.playerId, AiProfile.PRODUCTION)
            .respondToDecision(state, decision)
        repeat(3) {
            factory.create(decision.playerId, AiProfile.PRODUCTION).respondToDecision(state, decision) shouldBe expected
        }
        driver.submitSuccess(SubmitDecision(decision.playerId, expected))
    }
})
