package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.CoatiScavenger
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Coati Scavenger (LCI #179): {2}{G} 3/2 Creature — Raccoon
 * "Descend 4 — When this creature enters, if there are four or more permanent cards in your
 * graveyard, return target permanent card from your graveyard to your hand."
 *
 * Tests:
 *  - With four or more permanent cards in the graveyard: ETB trigger fires, the controller
 *    selects a target permanent card from their graveyard, and it returns to their hand.
 *  - With fewer than four permanent cards in the graveyard: the intervening-if condition
 *    (Descend 4) is not met, the trigger does not fire, and graveyard remains unchanged.
 */
class CoatiScavengerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CoatiScavenger))
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
            startingPlayer = 0
        )
        return driver
    }

    test("ETB trigger fires and returns a permanent card to hand when four or more permanent cards are in the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe player

        // Seed the graveyard with exactly four permanent cards — meets the Descend 4 threshold.
        // The first one is the card we will choose to return to hand; the others pad the count.
        val returnTarget = driver.putCardInGraveyard(player, "Grizzly Bears")
        repeat(3) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        // Cast Coati Scavenger ({2}{G}).
        val coati = driver.putCardInHand(player, "Coati Scavenger")
        driver.giveColorlessMana(player, 2)
        driver.giveMana(player, Color.GREEN, 1)
        driver.castSpell(player, coati)
        // Coati Scavenger resolves → enters the battlefield → ETB trigger's intervening-if
        // condition is true (four permanent cards in graveyard) → trigger fires and pauses for
        // the controller to choose a target permanent card from their graveyard.
        driver.bothPass()

        val decision = driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(player, listOf(returnTarget)).isSuccess shouldBe true
        // ETB trigger resolves: return target permanent card from graveyard to hand.
        driver.bothPass()

        driver.getHand(player) shouldContain returnTarget
        driver.getGraveyard(player) shouldNotContain returnTarget
    }

    test("ETB trigger does not fire when fewer than four permanent cards are in the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe player

        // Seed the graveyard with only three permanent cards — one short of the Descend 4 threshold.
        val graveyardCards = List(3) { driver.putCardInGraveyard(player, "Grizzly Bears") }

        // Cast Coati Scavenger ({2}{G}).
        val coati = driver.putCardInHand(player, "Coati Scavenger")
        driver.giveColorlessMana(player, 2)
        driver.giveMana(player, Color.GREEN, 1)
        driver.castSpell(player, coati)
        // Coati Scavenger resolves → enters the battlefield → ETB trigger's intervening-if
        // condition is false (only three permanent cards in graveyard, not four) → trigger
        // does not fire; no ChooseTargetsDecision is issued.
        driver.bothPass()

        (driver.pendingDecision is ChooseTargetsDecision) shouldBe false

        // All three graveyard cards remain in the graveyard — nothing was returned.
        graveyardCards.forEach { driver.getGraveyard(player) shouldContain it }
    }
})
