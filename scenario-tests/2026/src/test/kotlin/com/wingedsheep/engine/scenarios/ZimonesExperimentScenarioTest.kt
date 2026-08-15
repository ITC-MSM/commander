package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.ZimonesExperiment
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Zimone's Experiment {3}{G} Sorcery — look at the top five, reveal up to two creature and/or
 * land cards, lands enter tapped, creatures go to hand, the rest are bottomed at random.
 */
class ZimonesExperimentScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ZimonesExperiment)
        return driver
    }

    test("revealed land enters tapped, revealed creature goes to hand, rest are bottomed") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        // Top five (top-down): a creature, a land, then three filler cards.
        driver.putCardOnTopOfLibrary(me, "Forest")
        driver.putCardOnTopOfLibrary(me, "Forest")
        driver.putCardOnTopOfLibrary(me, "Lightning Bolt")
        val land = driver.putCardOnTopOfLibrary(me, "Forest")
        val creature = driver.putCardOnTopOfLibrary(me, "Centaur Courser")

        val spell = driver.putCardInHand(me, "Zimone's Experiment")
        driver.giveMana(me, Color.GREEN, 1)
        driver.giveColorlessMana(me, 3)
        driver.submit(
            CastSpell(playerId = me, cardId = spell, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass() // resolve -> look at five -> pause for the up-to-two selection

        driver.isPaused shouldBe true
        val select = driver.pendingDecision
        select.shouldBeInstanceOf<SelectCardsDecision>()
        // Among the five, only creature + land cards are eligible (the instant is excluded).
        // Top five = Centaur Courser, Forest, Lightning Bolt, Forest, Forest → 4 eligible.
        select.options.size shouldBe 4

        driver.submitDecision(
            me,
            CardsSelectedResponse(decisionId = select.id, selectedCards = listOf(creature, land))
        )
        driver.isPaused shouldBe false

        // Creature went to hand.
        driver.getHand(me).contains(creature) shouldBe true
        // Land entered the battlefield tapped.
        driver.getLands(me).contains(land) shouldBe true
        driver.isTapped(land) shouldBe true
    }

    test("may reveal nothing — all five go to the bottom") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val handBefore = driver.getHand(me).size
        val landsBefore = driver.getLands(me).size

        val spell = driver.putCardInHand(me, "Zimone's Experiment")
        driver.giveMana(me, Color.GREEN, 1)
        driver.giveColorlessMana(me, 3)
        driver.submit(
            CastSpell(playerId = me, cardId = spell, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.isPaused shouldBe true
        val select = driver.pendingDecision
        select.shouldBeInstanceOf<SelectCardsDecision>()
        // Decline: reveal nothing.
        driver.submitDecision(
            me,
            CardsSelectedResponse(decisionId = select.id, selectedCards = emptyList())
        )
        driver.isPaused shouldBe false

        // Nothing added to hand (besides the spell leaving) or to the battlefield.
        driver.getHand(me).size shouldBe handBefore
        driver.getLands(me).size shouldBe landsBefore
    }
})
