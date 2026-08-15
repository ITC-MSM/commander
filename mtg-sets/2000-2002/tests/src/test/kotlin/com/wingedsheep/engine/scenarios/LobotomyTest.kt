package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.Lobotomy
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Lobotomy (INV #255) — Invasion engine gap #10, the "capture a card's name" half.
 *
 * "Target player reveals their hand, then you choose a card other than a basic land card
 * from it. Search that player's graveyard, hand, and library for all cards with the same
 * name as the chosen card and exile them. Then that player shuffles."
 */
class LobotomyTest : FunSpec({

    test("exiles every card sharing the chosen card's name across all three zones") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Lobotomy))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opp = driver.getOpponent(you)

        // Opponent has Lightning Bolt copies in hand, graveyard, and library, plus
        // unrelated cards (and a basic land that must not be selectable).
        val boltInHand = driver.putCardInHand(opp, "Lightning Bolt")
        driver.putCardInHand(opp, "Stoke the Flames")
        driver.putCardInHand(opp, "Plains")
        driver.putCardInGraveyard(opp, "Lightning Bolt")
        driver.putCardInGraveyard(opp, "Stoke the Flames")
        driver.putCardOnTopOfLibrary(opp, "Lightning Bolt")
        driver.putCardOnTopOfLibrary(opp, "Stoke the Flames")

        val lobotomy = driver.putCardInHand(you, "Lobotomy")
        driver.giveMana(you, Color.BLUE, 1)
        driver.giveMana(you, Color.BLACK, 1)
        driver.giveMana(you, Color.WHITE, 2)
        driver.castSpell(you, lobotomy, targets = listOf(opp))
        driver.bothPass()

        // Resolving pauses for the controller to choose a card from the revealed hand.
        driver.submitCardSelection(you, listOf(boltInHand))

        driver.isPaused shouldBe false
        // All three Lightning Bolts are exiled; nothing named otherwise is touched.
        driver.getExileCardNames(opp).count { it == "Lightning Bolt" } shouldBe 3
        driver.getExileCardNames(opp) shouldNotContain "Stoke the Flames"
        driver.findCardsInHand(opp, "Lightning Bolt").size shouldBe 0
        driver.getGraveyardCardNames(opp) shouldContain "Stoke the Flames"
        driver.getGraveyardCardNames(opp) shouldNotContain "Lightning Bolt"
        // The basic land was never eligible and stays in hand.
        (driver.findCardInHand(opp, "Plains") != null) shouldBe true
    }
})
