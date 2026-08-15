package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.AnotherChance
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Another Chance (LCI #90) — {2}{B} Instant.
 *
 * "You may mill two cards. Then return up to two creature cards from your graveyard to your hand."
 *
 * Cases covered:
 *  1. Mill accepted → two cards go from library to graveyard, then two creature cards are
 *     returned from graveyard to hand.
 *  2. Mill declined → library is untouched, but the graveyard-return step still runs and
 *     a creature card is returned to hand.
 *  3. "Up to" is truly optional → selecting zero cards from the graveyard is legal (skip).
 *  4. No creatures in graveyard → selection prompt is auto-skipped (empty collection).
 */
class AnotherChanceScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(AnotherChance)
        driver.initMirrorMatch(Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Advance the stack through all priority windows until it is empty or paused. */
    fun GameTestDriver.drainStack(maxIterations: Int = 30) {
        var guard = 0
        while (guard++ < maxIterations && state.stack.isNotEmpty() && !isPaused) {
            bothPass()
        }
    }

    test("mill accepted: two cards milled, then two creature cards returned to hand") {
        val driver = newDriver()
        val me = driver.player1

        // Seed library top: two lands to mill (non-creatures so graveyard starts creature-clean).
        val millCard1 = driver.putCardOnTopOfLibrary(me, "Swamp")
        val millCard2 = driver.putCardOnTopOfLibrary(me, "Swamp")

        // Two creature cards already in graveyard to return.
        val creature1 = driver.putCardInGraveyard(me, "Glory Seeker")
        val creature2 = driver.putCardInGraveyard(me, "Grizzly Bears")

        val spell = driver.putCardInHand(me, "Another Chance")
        driver.giveMana(me, Color.BLACK, 1)
        driver.giveColorlessMana(me, 2)
        driver.castSpell(me, spell)
        driver.drainStack() // spell resolves, pauses at MayEffect

        // 1st pause: "You may mill two cards?" — answer yes.
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(me, true)
        driver.drainStack() // mill resolves, pauses at SelectFromCollection

        // Library loses two cards.
        driver.state.getLibrary(me) shouldNotContain millCard1
        driver.state.getLibrary(me) shouldNotContain millCard2

        // 2nd pause: choose up to two creature cards from graveyard to return.
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(me, listOf(creature1, creature2))
        driver.drainStack()

        // Both creatures are now in hand.
        driver.getHand(me) shouldContain creature1
        driver.getHand(me) shouldContain creature2
        driver.getGraveyard(me) shouldNotContain creature1
        driver.getGraveyard(me) shouldNotContain creature2
    }

    test("mill declined: library untouched, creature card still returned from graveyard") {
        val driver = newDriver()
        val me = driver.player1

        val creature = driver.putCardInGraveyard(me, "Glory Seeker")
        val libSizeBefore = driver.state.getLibrary(me).size

        val spell = driver.putCardInHand(me, "Another Chance")
        driver.giveMana(me, Color.BLACK, 1)
        driver.giveColorlessMana(me, 2)
        driver.castSpell(me, spell)
        driver.drainStack()

        // Decline the mill.
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(me, false)
        driver.drainStack()

        // Library is untouched.
        driver.state.getLibrary(me).size shouldBe libSizeBefore

        // Return-from-graveyard still runs: pick the one creature card.
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(me, listOf(creature))
        driver.drainStack()

        driver.getHand(me) shouldContain creature
        driver.getGraveyard(me) shouldNotContain creature
    }

    test("up to two means zero is valid: declining the graveyard selection leaves cards in place") {
        val driver = newDriver()
        val me = driver.player1

        val creature = driver.putCardInGraveyard(me, "Glory Seeker")

        val spell = driver.putCardInHand(me, "Another Chance")
        driver.giveMana(me, Color.BLACK, 1)
        driver.giveColorlessMana(me, 2)
        driver.castSpell(me, spell)
        driver.drainStack()

        // Decline mill.
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(me, false)
        driver.drainStack()

        // Choose zero creature cards (submit empty selection — "up to" allows it).
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(me, emptyList())
        driver.drainStack()

        // Creature stays in graveyard.
        driver.getGraveyard(me) shouldContain creature
        driver.getHand(me) shouldNotContain creature
    }

    test("no creatures in graveyard: selection is auto-skipped after mill") {
        val driver = newDriver()
        val me = driver.player1

        // Graveyard has only a non-creature card (land); no creature cards to gather.
        driver.putCardInGraveyard(me, "Swamp")
        val handSizeBefore = driver.getHand(me).size

        val spell = driver.putCardInHand(me, "Another Chance")
        driver.giveMana(me, Color.BLACK, 1)
        driver.giveColorlessMana(me, 2)
        driver.castSpell(me, spell)
        driver.drainStack()

        // MayEffect yes/no.
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(me, true)
        driver.drainStack()

        // Stack should now be fully resolved — no selection prompt because creature collection
        // is empty (SelectFromCollectionExecutor short-circuits on an empty collection).
        driver.state.stack.size shouldBe 0
        driver.isPaused shouldBe false
        // Hand did not gain any creature cards.
        driver.getHand(me).size shouldBe handSizeBefore
    }
})
