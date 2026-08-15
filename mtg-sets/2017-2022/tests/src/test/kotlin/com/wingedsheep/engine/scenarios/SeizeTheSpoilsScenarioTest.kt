package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.khm.cards.SeizeTheSpoils
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Seize the Spoils {2}{R} Sorcery (KHM canonical; reprinted in SOS/FDN).
 *
 * As an additional cost to cast this spell, discard a card.
 * Draw two cards and create a Treasure token.
 */
class SeizeTheSpoilsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SeizeTheSpoils, PredefinedTokens.Treasure))
        return driver
    }

    test("discards one card as an additional cost, then draws two and makes a Treasure") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val fodder = driver.putCardInHand(me, "Grizzly Bears")
        driver.giveMana(me, Color.RED, 3) // {R} + {2} generic
        val spell = driver.putCardInHand(me, "Seize the Spoils")
        val handWithSpell = driver.getHandSize(me)

        // The additional cost (discard a card) is paid up front at cast time.
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                additionalCostPayment = AdditionalCostPayment(discardedCards = listOf(fodder))
            )
        ).error shouldBe null
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        // Hand: -1 cast, -1 discard, +2 draw → handWithSpell.
        driver.getHandSize(me) shouldBe handWithSpell
        // A Treasure token now sits on the battlefield.
        driver.findPermanent(me, "Treasure") shouldNotBe null
    }
})
