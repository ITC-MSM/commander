package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.eld.cards.LindenTheSteadfastQueen
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Linden, the Steadfast Queen (ELD, reprinted in FDN) — {W}{W}{W} Legendary Creature — Human Noble, 3/3.
 *
 * - Vigilance.
 * - "Whenever a white creature you control attacks, you gain 1 life."
 *
 * The trigger uses [com.wingedsheep.sdk.scripting.TriggerBinding.ANY] over a white-creature-you-control
 * filter, so it fires once per qualifying attacker — including Linden herself, who is white. These tests
 * pin: a white attacker gains 1 life, a non-white attacker gains nothing, and multiple white attackers
 * (Linden + another) each add a point.
 */
class LindenTheSteadfastQueenScenarioTest : FunSpec({

    val whiteBear = card("Test White Bear") {
        manaCost = "{1}{W}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }
    val greenBear = card("Test Green Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(LindenTheSteadfastQueen, whiteBear, greenBear))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        return driver
    }

    fun GameTestDriver.resolveStack() {
        var guard = 0
        while (state.stack.isNotEmpty() && guard < 50) {
            bothPass()
            guard++
        }
    }

    fun GameTestDriver.attackWith(attackers: List<EntityId>) {
        val me = activePlayer!!
        val opponent = getOpponent(me)
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        declareAttackers(me, attackers, opponent)
        resolveStack()
    }

    test("Linden has vigilance") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val linden = driver.putCreatureOnBattlefield(me, "Linden, the Steadfast Queen")
        driver.state.projectedState.hasKeyword(linden, Keyword.VIGILANCE) shouldBe true
    }

    test("attacking with a white creature you control gains 1 life") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val bear = driver.putCreatureOnBattlefield(me, "Test White Bear")
        driver.removeSummoningSickness(bear)
        driver.putCreatureOnBattlefield(me, "Linden, the Steadfast Queen") // Linden stays back

        val before = driver.getLifeTotal(me)
        driver.attackWith(listOf(bear))
        driver.getLifeTotal(me) shouldBe before + 1
    }

    test("attacking with a non-white creature you control gains no life") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val bear = driver.putCreatureOnBattlefield(me, "Test Green Bear")
        driver.removeSummoningSickness(bear)
        driver.putCreatureOnBattlefield(me, "Linden, the Steadfast Queen")

        val before = driver.getLifeTotal(me)
        driver.attackWith(listOf(bear))
        driver.getLifeTotal(me) shouldBe before
    }

    test("Linden and another white creature attacking each add 1 life") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val linden = driver.putCreatureOnBattlefield(me, "Linden, the Steadfast Queen")
        val bear = driver.putCreatureOnBattlefield(me, "Test White Bear")
        driver.removeSummoningSickness(linden)
        driver.removeSummoningSickness(bear)

        val before = driver.getLifeTotal(me)
        driver.attackWith(listOf(linden, bear))
        driver.getLifeTotal(me) shouldBe before + 2
    }
})
