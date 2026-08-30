package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

private val TwoModeSpell: CardDefinition = card("Test Two Mode Spell") {
    manaCost = "{G}"
    typeLine = "Sorcery"
    spell {
        modal(chooseCount = 1) {
            mode("Draw a card", Effects.DrawCards(1))
            mode("Gain 1 life", Effects.GainLife(1))
        }
    }
}

private data class BoltFixture(
    val registry: CardRegistry,
    val driver: GameTestDriver,
    val caster: EntityId,
    val opponent: EntityId,
    val cast: CastSpell,
)

private fun boltFixture(opponentLife: Int = 20): BoltFixture {
    val registry = CardRegistry().apply { register(TestCards.all) }
    val driver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(Deck.of("Mountain" to 20), startingLife = 20)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }
    val caster = driver.player1
    val opponent = driver.player2
    driver.setLifeTotal(opponent, opponentLife)
    val bolt = driver.putCardInHand(caster, "Lightning Bolt")
    driver.giveMana(caster, Color.RED, 1)
    return BoltFixture(
        registry = registry,
        driver = driver,
        caster = caster,
        opponent = opponent,
        cast = CastSpell(
            playerId = caster,
            cardId = bolt,
            targets = listOf(ChosenTarget.Player(opponent)),
            paymentStrategy = PaymentStrategy.FromPool,
        ),
    )
}

class GameSimulatorTest : FunSpec({

    test("automatic transition exhaustion is not successful simulation completion") {
        val fixture = boltFixture()
        val result = GameSimulator(fixture.registry, maxAutomaticTransitions = 1)
            .simulate(fixture.driver.state, fixture.cast)

        val stopped = result.shouldBeInstanceOf<SimulationResult.StoppedAtLimit>()
        withClue("one automatic pass leaves Lightning Bolt unresolved on the stack") {
            stopped.state.stack.shouldNotBeEmpty()
            stopped.state.gameOver.shouldBeFalse()
        }
        stopped.automaticTransitions.shouldBeExactly(1)
        stopped.limit.shouldBeExactly(1)
        shouldThrow<AutomaticResolutionLimitException> {
            stopped.requireNoAutomaticResolutionStop("Test evaluation")
        }.message shouldContain "retained simulation state is unfinished"
    }

    test("ordinary quiet simulation is Terminal without ending the game") {
        val fixture = boltFixture()

        val terminal = GameSimulator(fixture.registry, maxAutomaticTransitions = 2)
            .simulate(fixture.driver.state, fixture.cast)
            .shouldBeInstanceOf<SimulationResult.Terminal>()

        terminal.state.stack.isEmpty().shouldBeTrue()
        terminal.state.gameOver.shouldBeFalse()
    }

    test("a genuine game end remains Terminal and is identified by gameOver") {
        val fixture = boltFixture(opponentLife = 3)

        val terminal = GameSimulator(fixture.registry, maxAutomaticTransitions = 2)
            .simulate(fixture.driver.state, fixture.cast)
            .shouldBeInstanceOf<SimulationResult.Terminal>()

        terminal.state.gameOver.shouldBeTrue()
        terminal.state.winnerId shouldBe fixture.caster
    }

    test("a genuine unresolved choice remains NeedsDecision") {
        val registry = CardRegistry().apply { register(TestCards.all + TwoModeSpell) }
        val driver = GameTestDriver().apply {
            registerCards(TestCards.all + TwoModeSpell)
            initMirrorMatch(Deck.of("Forest" to 20), startingLife = 20)
            passPriorityUntil(Step.PRECOMBAT_MAIN)
        }
        val caster = driver.player1
        val spell = driver.putCardInHand(caster, TwoModeSpell.name)
        driver.giveMana(caster, Color.GREEN, 1)

        GameSimulator(registry, maxAutomaticTransitions = 1).simulate(
            driver.state,
            CastSpell(caster, spell, paymentStrategy = PaymentStrategy.FromPool),
        ).shouldBeInstanceOf<SimulationResult.NeedsDecision>()
    }

    test("an engine rejection remains Illegal") {
        val fixture = boltFixture()

        val illegal = GameSimulator(fixture.registry)
            .simulate(fixture.driver.state, PassPriority(fixture.opponent))
            .shouldBeInstanceOf<SimulationResult.Illegal>()

        illegal.reason shouldContain "priority"
    }
})
