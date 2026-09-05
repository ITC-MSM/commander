package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.core.SubmitDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class AIPlayerFactoryTest : FunSpec({
    fun game() = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

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
        fun field(value: Any, name: String): Any? = value.javaClass.getDeclaredField(name).apply {
            isAccessible = true
        }.get(value)
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

    test("fresh players preserve responses and simulation through pending choices") {
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
        driver.castSpell(actor, id).isSuccess shouldBe true
        val beforeResolution = driver.state
        val factory = AIPlayer.Factory(driver.cardRegistry)
        // Choosing on the live stack invokes the simulator's nested decision resolver.
        factory.create(actor, AiProfile.PRODUCTION).chooseAction(beforeResolution) shouldBe
            AIPlayer.create(driver.cardRegistry, actor, AiProfile.PRODUCTION).chooseAction(beforeResolution)
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
