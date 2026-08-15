package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Splitskin Doll (DSK #33) — {1}{W} Artifact Creature — Toy 2/1.
 *
 * "When this creature enters, draw a card. Then discard a card unless you control another creature
 *  with power 2 or less."
 *
 * Exercises the enters trigger draw + the conditional discard gated on
 * `Conditions.Not(YouControl(Creature.powerAtMost(2), excludeSelf = true))` — the discard happens
 * only when you don't control ANOTHER creature with power 2 or less (the Doll itself, power 2, must
 * not satisfy the clause).
 */
class SplitskinDollScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("no other low-power creature: draw then must discard") {
        val driver = newDriver()
        val player = driver.player1

        // A 3/3 (power > 2) does NOT satisfy the "power 2 or less" clause.
        driver.putCreatureOnBattlefield(player, "Centaur Courser")

        val doll = driver.putCardInHand(player, "Splitskin Doll")
        driver.giveMana(player, Color.WHITE, 2)
        val handBefore = driver.getHandSize(player) - 1 // the doll itself leaves hand on cast

        driver.castSpell(player, doll).isSuccess shouldBe true
        driver.bothPass() // resolve the creature spell -> enters trigger goes on the stack
        driver.bothPass() // resolve the enters trigger: draw, then conditional discard

        // The draw happened, then a discard is required (no other power<=2 creature).
        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitDecision(player, CardsSelectedResponse(decision.id, listOf(driver.getHand(player).first())))
        driver.isPaused shouldBe false

        // Net hand size: +1 draw, -1 discard = unchanged from pre-cast (excluding the doll).
        driver.getHandSize(player) shouldBe handBefore
    }

    test("another power-2-or-less creature: draw with no discard") {
        val driver = newDriver()
        val player = driver.player1

        // A 1/1 (power 2 or less) satisfies the "unless" clause, so no discard.
        driver.putCreatureOnBattlefield(player, "Savannah Lions")

        val doll = driver.putCardInHand(player, "Splitskin Doll")
        driver.giveMana(player, Color.WHITE, 2)
        val handBefore = driver.getHandSize(player) - 1

        driver.castSpell(player, doll).isSuccess shouldBe true
        driver.bothPass() // resolve the creature spell
        driver.bothPass() // resolve the enters trigger: draw only, no discard

        driver.pendingDecision shouldBe null
        driver.isPaused shouldBe false
        // Only the draw happened: +1 over pre-cast hand.
        driver.getHandSize(player) shouldBe handBefore + 1
    }
})
