package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.MindIntoMatter
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Mind into Matter — {X}{G}{U} Sorcery.
 * Draw X cards. Then you may put a permanent card with mana value X or less from your hand onto the
 * battlefield tapped.
 *
 * Verifies the chosen X drives both halves: the draw count and the "mana value X or less" filter on
 * the optional put-from-hand-onto-battlefield-tapped step.
 */
class MindIntoMatterScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + MindIntoMatter)
        return driver
    }

    test("X=2 draws two and lets a MV<=2 permanent enter tapped; a MV>2 card is not selectable") {
        val driver = createDriver()
        // Library has Grizzly Bears ({1}{G}, MV 2) on top, then a 5-mana Force of Nature.
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Force of Nature ({2}{G}{G}{G}, MV 5) will be drawn but is too expensive to put down at X=2.
        driver.putCardOnTopOfLibrary(player, "Force of Nature")
        driver.putCardOnTopOfLibrary(player, "Grizzly Bears")

        val spell = driver.putCardInHand(player, "Mind into Matter")
        val handBefore = driver.getHand(player).size

        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.BLUE, 1)
        driver.giveColorlessMana(player, 2) // pays {X} with X=2

        driver.castXSpell(player, spell, xValue = 2)
        driver.bothPass() // resolve → draw 2, then pause on the optional put selection

        // Hand grew by 2 (drawn) minus the spell that left hand (net +1 from before-cast hand,
        // but we measure after cast so just confirm the drawn cards arrived).
        driver.getHand(player).size shouldBe (handBefore - 1 + 2)

        val decision = driver.pendingDecision as SelectCardsDecision
        decision.minSelections shouldBe 0
        // Grizzly Bears (MV 2) is a legal choice; Force of Nature (MV 5) is not.
        val bears = decision.options.firstOrNull { driver.getCardName(it) == "Grizzly Bears" }
        bears shouldNotBe null
        decision.options.none { driver.getCardName(it) == "Force of Nature" } shouldBe true

        driver.submitCardSelection(player, listOf(bears!!))
        driver.isPaused shouldBe false

        // The chosen Grizzly Bears is on the battlefield, tapped.
        val bearsOnBattlefield = driver.findPermanent(player, "Grizzly Bears")
        bearsOnBattlefield shouldBe bears
        driver.isTapped(bears) shouldBe true
    }

    test("the put is optional — declining leaves the drawn cards in hand") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spell = driver.putCardInHand(player, "Mind into Matter")
        // A Plains (MV 0) in hand is a legal put candidate at X=1, so the optional select pauses.
        val plains = driver.putCardInHand(player, "Plains")
        driver.giveMana(player, Color.GREEN, 1)
        driver.giveMana(player, Color.BLUE, 1)
        driver.giveColorlessMana(player, 1) // X=1

        driver.castXSpell(player, spell, xValue = 1)
        driver.bothPass()

        val decision = driver.pendingDecision as SelectCardsDecision
        decision.options.contains(plains) shouldBe true
        driver.submitCardSelection(player, emptyList()) // decline

        driver.isPaused shouldBe false
        // Declined: the Plains stays in hand, nothing entered the battlefield from the put.
        driver.getHand(player).contains(plains) shouldBe true
        driver.findPermanent(player, "Plains") shouldBe null
    }
})
