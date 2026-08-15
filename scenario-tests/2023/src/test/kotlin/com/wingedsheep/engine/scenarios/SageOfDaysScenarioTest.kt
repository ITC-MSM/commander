package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SageOfDays
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.engine.state.ZoneKey
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Sage of Days (LCI #73): {2}{U} 3/2 Creature — Human Wizard
 * "When this creature enters, look at the top three cards of your library. You may put one of those
 * cards back on top of your library. Put the rest into your graveyard."
 *
 * Tests:
 *  - The ETB trigger looks at the top three cards; the controller keeps one on top and the other
 *    two go to the graveyard.
 *  - The selection is optional (minSelections = 0): declining sends all three to the graveyard.
 */
class SageOfDaysScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SageOfDays))
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
            startingPlayer = 0
        )
        return driver
    }

    fun libraryTop(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId) =
        driver.state.getZone(ZoneKey(player, Zone.LIBRARY)).first()

    test("keeps the chosen card on top of the library and puts the other two into the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe player

        // Stack the top three (last call = top of library):
        //   position 3 (deepest of the three): the card we send to the graveyard
        //   position 2                       : the card we send to the graveyard
        //   position 1 (top)                 : the card we keep on top
        val bottom = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val middle = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val keep = driver.putCardOnTopOfLibrary(player, "Forest")

        // Cast Sage of Days ({2}{U}).
        val sage = driver.putCardInHand(player, "Sage of Days")
        driver.giveColorlessMana(player, 2)
        driver.giveMana(player, Color.BLUE, 1)
        driver.castSpell(player, sage)
        // Resolve the creature spell, then its ETB trigger, pausing on the keep-one decision.
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()

        val decision = driver.pendingDecision as SelectCardsDecision
        decision.minSelections shouldBe 0
        decision.maxSelections shouldBe 1
        decision.options.toSet() shouldBe setOf(keep, middle, bottom)

        driver.submitCardSelection(player, listOf(keep))
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()
        driver.isPaused shouldBe false

        // The kept card is back on top of the library; the other two are in the graveyard.
        libraryTop(driver, player) shouldBe keep
        driver.getGraveyard(player) shouldContain middle
        driver.getGraveyard(player) shouldContain bottom
        driver.getGraveyard(player) shouldNotContain keep
    }

    test("declining the optional keep sends all three looked-at cards to the graveyard") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe player

        val c1 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val c2 = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val c3 = driver.putCardOnTopOfLibrary(player, "Forest")

        val sage = driver.putCardInHand(player, "Sage of Days")
        driver.giveColorlessMana(player, 2)
        driver.giveMana(player, Color.BLUE, 1)
        driver.castSpell(player, sage)
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()

        val decision = driver.pendingDecision as SelectCardsDecision
        decision.minSelections shouldBe 0
        // Keep nothing on top.
        driver.submitCardSelection(player, emptyList())
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()
        driver.isPaused shouldBe false

        // All three looked-at cards ended up in the graveyard.
        driver.getGraveyard(player) shouldContain c1
        driver.getGraveyard(player) shouldContain c2
        driver.getGraveyard(player) shouldContain c3
    }
})
