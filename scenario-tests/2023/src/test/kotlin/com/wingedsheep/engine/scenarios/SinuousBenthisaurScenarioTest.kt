package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.SinuousBenthisaur
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Sinuous Benthisaur (LCI #76): {5}{U} 4/4 Creature — Dinosaur
 * "When this creature enters, look at the top X cards of your library, where X is the number of
 *  Caves you control plus the number of Cave cards in your graveyard. Put two of those cards into
 *  your hand and the rest on the bottom of your library in a random order."
 *
 * Tests:
 *  - X = (Caves controlled) + (Cave cards in graveyard); with X = 3 the controller looks at the
 *    top three, keeps exactly two in hand, and the third goes to the bottom of the library.
 *  - Edge: X = 1 (one Cave, empty graveyard) — "put two" caps at the single looked-at card, which
 *    is put into hand with no decision and nothing left for the bottom.
 */
class SinuousBenthisaurScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SinuousBenthisaur))
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
            startingPlayer = 0
        )
        return driver
    }

    fun library(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId) =
        driver.state.getZone(ZoneKey(player, Zone.LIBRARY))

    test("X = 2 Caves controlled + 1 Cave in graveyard = 3: keep two, third goes to the bottom") {
        val driver = newDriver()
        val me = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe me

        // X = 2 Caves controlled + 1 Cave card in graveyard = 3.
        driver.putPermanentOnBattlefield(me, "Captivating Cave")
        driver.putPermanentOnBattlefield(me, "Captivating Cave")
        driver.putCardInGraveyard(me, "Captivating Cave")

        // Stack the top three of the library (last call = top).
        val deepest = driver.putCardOnTopOfLibrary(me, "Grizzly Bears")
        val middle = driver.putCardOnTopOfLibrary(me, "Grizzly Bears")
        val top = driver.putCardOnTopOfLibrary(me, "Island")

        // Cast Sinuous Benthisaur ({5}{U}).
        val benthisaur = driver.putCardInHand(me, "Sinuous Benthisaur")
        driver.giveColorlessMana(me, 5)
        driver.giveMana(me, Color.BLUE, 1)
        driver.castSpell(me, benthisaur)
        // Resolve the creature spell and its ETB trigger, pausing on the keep-two decision.
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()

        val decision = driver.pendingDecision as SelectCardsDecision
        decision.minSelections shouldBe 2
        decision.maxSelections shouldBe 2
        decision.options.toSet() shouldBe setOf(top, middle, deepest)

        // Keep two of the looked-at cards in hand; the third goes to the bottom.
        driver.submitCardSelection(me, listOf(top, middle))
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()
        driver.isPaused shouldBe false

        driver.getHand(me) shouldContainAll listOf(top, middle)
        driver.getHand(me) shouldNotContain deepest
        // The unkept card is on the bottom of the library.
        library(driver, me).last() shouldBe deepest
    }

    test("X = 1 (one Cave, empty graveyard): the single looked-at card is put into hand, none to bottom") {
        val driver = newDriver()
        val me = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe me

        // X = 1 Cave controlled + 0 Cave cards in graveyard = 1.
        driver.putPermanentOnBattlefield(me, "Captivating Cave")

        val onlyLook = driver.putCardOnTopOfLibrary(me, "Island")

        val benthisaur = driver.putCardInHand(me, "Sinuous Benthisaur")
        driver.giveColorlessMana(me, 5)
        driver.giveMana(me, Color.BLUE, 1)
        driver.castSpell(me, benthisaur)
        // "Put two" caps at the single card looked at → auto-selected, no decision to make.
        while (!driver.isPaused && driver.state.stack.isNotEmpty()) driver.bothPass()
        driver.isPaused shouldBe false

        // The looked-at card ends up in hand, and it is no longer on top of the library.
        driver.getHand(me) shouldContain onlyLook
        library(driver, me).firstOrNull() shouldNotBe onlyLook
    }
})
