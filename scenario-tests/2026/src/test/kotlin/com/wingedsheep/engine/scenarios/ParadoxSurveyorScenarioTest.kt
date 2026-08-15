package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.MindIntoMatter
import com.wingedsheep.mtg.sets.definitions.sos.cards.ParadoxSurveyor
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Paradox Surveyor — {G}{G/U}{U} Creature — Elf Druid 3/3, Reach.
 * When this creature enters, look at the top five cards of your library. You may reveal a land card
 * or a card with {X} in its mana cost from among them and put it into your hand. Put the rest on the
 * bottom of your library in a random order.
 *
 * Exercises the new `CardPredicate.HasXInManaCost` (a "card with {X} in its mana cost") OR'd with
 * land, surfaced as the optional reveal-to-hand selection.
 */
class ParadoxSurveyorScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        // Mind into Matter ({X}{G}{U}) is the {X}-in-cost card we expect to be revealable.
        driver.registerCards(TestCards.all + ParadoxSurveyor + MindIntoMatter)
        return driver
    }

    test("an {X}-cost card and a land are offered; chosen card goes to hand, rest to bottom") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Top five from the top: Grizzly Bears, Mind into Matter ({X}{G}{U}), Forest, Grizzly Bears, Grizzly Bears
        driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val land = driver.putCardOnTopOfLibrary(player, "Forest")
        val xCard = driver.putCardOnTopOfLibrary(player, "Mind into Matter")
        driver.putCardOnTopOfLibrary(player, "Grizzly Bears")

        val surveyor = driver.putCardInHand(player, "Paradox Surveyor")
        driver.giveMana(player, Color.GREEN, 2)
        driver.giveMana(player, Color.BLUE, 1)

        driver.castSpell(player, surveyor)
        driver.bothPass() // resolve creature; ETB trigger goes on the stack
        driver.bothPass() // resolve ETB trigger → pauses on the optional reveal selection

        val decision = driver.pendingDecision as SelectCardsDecision
        // Only the land and the {X}-cost card are selectable; plain Grizzly Bears are not.
        decision.options.contains(land) shouldBe true
        decision.options.contains(xCard) shouldBe true
        decision.minSelections shouldBe 0

        // Reveal the {X}-cost card → it goes to hand.
        driver.submitCardSelection(player, listOf(xCard))
        driver.isPaused shouldBe false

        driver.getHand(player).contains(xCard) shouldBe true
        // The land we didn't pick was bottomed, not in hand.
        driver.getHand(player).contains(land) shouldBe false
        val library = driver.state.getZone(ZoneKey(player, Zone.LIBRARY))
        library.contains(land) shouldBe true

        driver.findPermanent(player, "Paradox Surveyor") shouldNotBe null
    }

    test("the reveal is optional — declining keeps every looked-at card in the library") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putCardOnTopOfLibrary(player, "Forest")
        val surveyor = driver.putCardInHand(player, "Paradox Surveyor")
        driver.giveMana(player, Color.GREEN, 2)
        driver.giveMana(player, Color.BLUE, 1)

        driver.castSpell(player, surveyor)
        driver.bothPass()
        driver.bothPass()

        val decision = driver.pendingDecision as SelectCardsDecision
        decision.minSelections shouldBe 0
        driver.submitCardSelection(player, emptyList()) // decline

        driver.isPaused shouldBe false
        driver.getHand(player).contains(land) shouldBe false
        val library = driver.state.getZone(ZoneKey(player, Zone.LIBRARY))
        library.contains(land) shouldBe true
    }
})
