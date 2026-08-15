package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TypecycleCard
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.SpectralSnatcher
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Spectral Snatcher — {4}{B}{B} Creature — Spirit 6/5
 * Ward—Discard a card.
 * Swampcycling {2}.
 */
class SpectralSnatcherScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(SpectralSnatcher)
        return driver
    }

    test("Ward—Discard a card prompts the targeting opponent") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        driver.putCreatureOnBattlefield(opponent, "Spectral Snatcher")

        // I target it with Lightning Bolt → ward triggers, asking me to pay (discard a card).
        driver.putCardInHand(me, "Mountain") // a spare card to discard
        driver.giveMana(me, Color.RED, 1)
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        val snatcher = driver.findPermanent(opponent, "Spectral Snatcher")!!
        driver.castSpellWithTargets(me, bolt, listOf(ChosenTarget.Permanent(snatcher)))
        driver.bothPass()

        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        (driver.pendingDecision as YesNoDecision).playerId shouldBe me
    }

    test("Swampcycling {2} discards the card and searches for a Swamp") {
        val driver = createDriver()
        // Mountain deck so the lone Swamp on top is the unambiguous search target.
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val snatcher = driver.putCardInHand(me, "Spectral Snatcher")
        val swamp = driver.putCardOnTopOfLibrary(me, "Swamp")
        driver.giveColorlessMana(me, 2)

        val result = driver.submit(TypecycleCard(playerId = me, cardId = snatcher))
        (result.isSuccess || result.isPaused).shouldBeTrue()

        driver.getGraveyardCardNames(me) shouldContain "Spectral Snatcher"

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.options shouldContain swamp

        driver.submitDecision(me, CardsSelectedResponse(decision.id, listOf(swamp)))
        driver.findCardInHand(me, "Swamp") shouldBe swamp
    }
})
