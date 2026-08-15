package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Razorkin Needlehead — {R}{R} Creature — Human Assassin, 2/2
 *   "This creature has first strike during your turn.
 *    Whenever an opponent draws a card, this creature deals 1 damage to them."
 *
 * - The first-strike clause is a conditionally-active static ability
 *   (`GrantKeyword(FIRST_STRIKE, source())` gated on `Conditions.IsYourTurn`): present on your
 *   turn, absent on the opponent's.
 * - The draw-punisher is `Triggers.OpponentDraws` → `DealDamage(1, TriggeringPlayer, source = Self)`.
 *   The plain OpponentDraws variant has no draw-step exemption, so the opponent's turn-based
 *   for-turn draw fires it.
 */
class RazorkinNeedleheadScenarioTest : FunSpec({

    val projector = StateProjector()

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
    }

    test("has first strike during your turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val needlehead = d.putCreatureOnBattlefield(you, "Razorkin Needlehead")

        projector.project(d.state).hasKeyword(needlehead, Keyword.FIRST_STRIKE) shouldBe true
    }

    test("does NOT have first strike during the opponent's turn") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val needlehead = d.putCreatureOnBattlefield(you, "Razorkin Needlehead")

        // Advance into the opponent's turn.
        d.passPriorityUntil(Step.END)
        d.bothPass()
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        projector.project(d.state).hasKeyword(needlehead, Keyword.FIRST_STRIKE) shouldBe false
    }

    test("deals 1 damage to an opponent when they draw a card") {
        val d = driver()
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(you, "Razorkin Needlehead")
        d.setLifeTotal(opponent, 20)

        // Advance into the opponent's turn — their for-turn draw in the draw step fires the trigger.
        d.passPriorityUntil(Step.END)
        d.bothPass()
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        while (d.state.stack.isNotEmpty()) d.bothPass()

        d.getLifeTotal(opponent) shouldBe 19
    }
})
