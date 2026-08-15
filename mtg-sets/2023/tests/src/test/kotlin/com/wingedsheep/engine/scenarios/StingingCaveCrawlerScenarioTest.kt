package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.StingingCaveCrawler
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Stinging Cave Crawler (LCI #124) — {2}{B} Creature — Insect Horror 1/3.
 *
 * "Deathtouch
 *  Descend 4 — Whenever this creature attacks, if there are four or more permanent cards in your
 *  graveyard, you draw a card and you lose 1 life."
 *
 * Covered end-to-end:
 *  1. Descend 4 met (four permanent cards in the graveyard): attacking fires the trigger — the
 *     controller draws a card and loses 1 life.
 *  2. Descend 4 not met (three permanent cards): the intervening-if fails, so attacking draws no
 *     card and costs no life.
 */
class StingingCaveCrawlerScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(StingingCaveCrawler)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        return driver
    }

    test("Descend 4 met: attacking draws a card and loses 1 life") {
        val driver = newDriver()
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val crawler = driver.putCreatureOnBattlefield(attacker, "Stinging Cave Crawler")
        driver.removeSummoningSickness(crawler)

        // Four permanent cards in the graveyard — meets the Descend 4 threshold.
        repeat(4) { driver.putCardInGraveyard(attacker, "Swamp") }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val handBefore = driver.getHandSize(attacker)
        val lifeBefore = driver.getLifeTotal(attacker)

        driver.declareAttackers(attacker, listOf(crawler), defender)
        // The attack trigger goes on the stack; resolve it.
        driver.bothPass()

        driver.getHandSize(attacker) shouldBe handBefore + 1
        driver.getLifeTotal(attacker) shouldBe lifeBefore - 1
    }

    test("Descend 4 not met: attacking with only three permanent cards has no effect") {
        val driver = newDriver()
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val crawler = driver.putCreatureOnBattlefield(attacker, "Stinging Cave Crawler")
        driver.removeSummoningSickness(crawler)

        // Only three permanent cards — one short of Descend 4.
        repeat(3) { driver.putCardInGraveyard(attacker, "Swamp") }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val handBefore = driver.getHandSize(attacker)
        val lifeBefore = driver.getLifeTotal(attacker)

        driver.declareAttackers(attacker, listOf(crawler), defender)
        driver.bothPass()

        // Intervening-if failed: no draw, no life loss.
        driver.getHandSize(attacker) shouldBe handBefore
        driver.getLifeTotal(attacker) shouldBe lifeBefore
    }
})
