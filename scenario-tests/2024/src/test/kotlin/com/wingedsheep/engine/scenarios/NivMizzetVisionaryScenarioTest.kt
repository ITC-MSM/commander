package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.NivMizzetVisionary
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Niv-Mizzet, Visionary (FDN #123) — {4}{U}{R} Legendary Creature — Dragon Wizard, 5/5.
 *
 * "Flying. You have no maximum hand size. Whenever a source you control deals noncombat damage
 * to an opponent, you draw that many cards."
 *
 * The trigger reuses Twinflame Tyrant's "a source you control" shape (`DealsDamageEvent` with
 * `sourceFilter = GameObjectFilter.Any.youControl()`), scoped to noncombat damage at an opponent,
 * and draws the triggering damage amount (`ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT`).
 */
class NivMizzetVisionaryScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(NivMizzetVisionary)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("noncombat damage from a source you control draws that many cards") {
        val d = newDriver()
        val p1 = d.player1
        val p2 = d.player2

        d.putCreatureOnBattlefield(p1, "Niv-Mizzet, Visionary")
        val bolt = d.putCardInHand(p1, "Lightning Bolt") // {R}: 3 damage to any target
        d.giveMana(p1, Color.RED, 1)

        val handBefore = d.getHandSize(p1)

        // Cast Lightning Bolt at the opponent — 3 noncombat damage from a source you control.
        d.castSpell(p1, bolt, targets = listOf(p2)).error shouldBe null
        d.bothPass() // Lightning Bolt resolves, deals 3, Niv's trigger goes on the stack
        d.bothPass() // Niv's trigger resolves, drawing 3

        d.isPaused shouldBe false
        d.getLifeTotal(p2) shouldBe 17
        // -1 for the cast Lightning Bolt, +3 drawn by Niv-Mizzet.
        d.getHandSize(p1) shouldBe handBefore - 1 + 3
    }

    test("noncombat damage to an opponent's creature does not trigger the draw") {
        val d = newDriver()
        val p1 = d.player1
        val p2 = d.player2

        d.putCreatureOnBattlefield(p1, "Niv-Mizzet, Visionary")
        val victim = d.putCreatureOnBattlefield(p2, "Centaur Courser") // 3/3
        val bolt = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)

        val handBefore = d.getHandSize(p1)

        // Damage to a permanent an opponent controls is not damage "to an opponent" (a player).
        d.castSpell(p1, bolt, targets = listOf(victim)).error shouldBe null
        d.bothPass()

        d.isPaused shouldBe false
        d.assertInGraveyard(p2, "Centaur Courser")
        // Only the Lightning Bolt left the hand; Niv did not trigger.
        d.getHandSize(p1) shouldBe handBefore - 1
    }
})
