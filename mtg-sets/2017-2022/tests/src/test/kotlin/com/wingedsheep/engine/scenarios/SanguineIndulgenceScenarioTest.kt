package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Sanguine Indulgence (M21 #121).
 *
 * {3}{B} Sorcery
 * "This spell costs {3} less to cast if you've gained 3 or more life this turn.
 *  Return up to two target creature cards from your graveyard to your hand."
 *
 * Covers the life-gain-gated discount (which needs a real life-gain event, not a life-total
 * poke, since it reads the `LIFE_GAINED` turn tracker) and the "up to two" return.
 */
class SanguineIndulgenceScenarioTest : FunSpec({

    // Minimal life-gain spells so the LIFE_GAINED turn tracker is exercised for real.
    val GainThree = card("Test Gain Three") {
        manaCost = "{W}"
        typeLine = "Instant"
        spell { effect = Effects.GainLife(3) }
    }
    val GainTwo = card("Test Gain Two") {
        manaCost = "{W}"
        typeLine = "Instant"
        spell { effect = Effects.GainLife(2) }
    }

    fun createRegistry(): CardRegistry = CardRegistry().apply {
        register(TestCards.all)
        register(GainThree)
        register(GainTwo)
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(GainThree)
        driver.registerCard(GainTwo)
        return driver
    }

    fun gainLife(driver: GameTestDriver, playerId: com.wingedsheep.sdk.model.EntityId, cardName: String) {
        val spell = driver.putCardInHand(playerId, cardName)
        driver.giveMana(playerId, Color.WHITE, 1)
        driver.castSpell(playerId, spell).error shouldBe null
        driver.bothPass()
    }

    test("costs {3} less after gaining 3 or more life this turn") {
        val registry = createRegistry()
        val calculator = CostCalculator(registry)

        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val indulgence = registry.requireCard("Sanguine Indulgence")

        // Before any life gain: full {3}{B}.
        calculator.calculateEffectiveCost(driver.state, indulgence, you).genericAmount shouldBe 3

        gainLife(driver, you, "Test Gain Three")

        // After gaining 3: the whole generic component is shaved off, leaving {B}.
        val reduced = calculator.calculateEffectiveCost(driver.state, indulgence, you)
        reduced.genericAmount shouldBe 0
        reduced.cmc shouldBe 1
    }

    test("gaining only 2 life this turn is not enough") {
        val registry = createRegistry()
        val calculator = CostCalculator(registry)

        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        gainLife(driver, you, "Test Gain Two")

        val indulgence = registry.requireCard("Sanguine Indulgence")
        calculator.calculateEffectiveCost(driver.state, indulgence, you).genericAmount shouldBe 3
    }

    test("returns both targeted creature cards from your graveyard to your hand") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCardInGraveyard(you, "Grizzly Bears")
        val giant = driver.putCardInGraveyard(you, "Hill Giant")

        val spell = driver.putCardInHand(you, "Sanguine Indulgence")
        driver.giveMana(you, Color.BLACK, 4)
        driver.castSpellWithTargets(
            you,
            spell,
            targets = listOf(
                ChosenTarget.Card(bears, you, Zone.GRAVEYARD),
                ChosenTarget.Card(giant, you, Zone.GRAVEYARD),
            ),
        ).error shouldBe null
        driver.bothPass()

        driver.getGraveyardCardNames(you).contains("Grizzly Bears") shouldBe false
        driver.getGraveyardCardNames(you).contains("Hill Giant") shouldBe false
        driver.getHand(you).contains(bears) shouldBe true
        driver.getHand(you).contains(giant) shouldBe true
    }
})
