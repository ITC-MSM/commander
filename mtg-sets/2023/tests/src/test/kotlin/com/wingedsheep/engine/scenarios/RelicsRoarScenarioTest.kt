package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Relic's Roar (LCI #71).
 *
 * {U} Instant
 * "Until end of turn, target artifact or creature becomes a Dinosaur artifact creature with base
 * power and toughness 4/3 in addition to its other types."
 */
class RelicsRoarScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("target creature becomes a 4/3 Dinosaur artifact creature, keeping its other types") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A 3/3 Centaur Warrior creature.
        val courser = driver.putCreatureOnBattlefield(activePlayer, "Centaur Courser")
        val spell = driver.putCardInHand(activePlayer, "Relic's Roar")
        driver.giveMana(activePlayer, Color.BLUE, 1)

        val castResult = driver.castSpell(activePlayer, spell, targets = listOf(courser))
        castResult.isSuccess shouldBe true
        driver.bothPass()

        val projected = projector.project(driver.state)
        // Base P/T is now 4/3.
        projected.getPower(courser) shouldBe 4
        projected.getToughness(courser) shouldBe 3
        // Gains the artifact and creature card types.
        projected.hasType(courser, "ARTIFACT") shouldBe true
        projected.hasType(courser, "CREATURE") shouldBe true
        // Gains Dinosaur "in addition to its other types" — keeps its printed creature types.
        projected.hasSubtype(courser, "DINOSAUR") shouldBe true
        projected.hasSubtype(courser, "CENTAUR") shouldBe true
        projected.hasSubtype(courser, "WARRIOR") shouldBe true
    }

    test("target noncreature artifact becomes a 4/3 Dinosaur artifact creature and reverts at end of turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A noncreature artifact.
        val artifact = driver.putPermanentOnBattlefield(activePlayer, "Runaway Boulder")
        val spell = driver.putCardInHand(activePlayer, "Relic's Roar")
        driver.giveMana(activePlayer, Color.BLUE, 1)

        driver.castSpell(activePlayer, spell, targets = listOf(artifact))
        driver.bothPass()

        // Mid-turn: the artifact is now a 4/3 Dinosaur artifact creature.
        val midTurn = projector.project(driver.state)
        midTurn.hasType(artifact, "CREATURE") shouldBe true
        midTurn.hasType(artifact, "ARTIFACT") shouldBe true
        midTurn.hasSubtype(artifact, "DINOSAUR") shouldBe true
        midTurn.getPower(artifact) shouldBe 4
        midTurn.getToughness(artifact) shouldBe 3

        // Advance to the opponent's upkeep — end-of-turn cleanup expires the effect.
        driver.passPriorityUntil(Step.UPKEEP)

        val nextTurn = projector.project(driver.state)
        nextTurn.hasType(artifact, "CREATURE") shouldBe false
        nextTurn.hasSubtype(artifact, "DINOSAUR") shouldBe false
        // Still an artifact (its printed type is unchanged).
        nextTurn.hasType(artifact, "ARTIFACT") shouldBe true
    }
})
