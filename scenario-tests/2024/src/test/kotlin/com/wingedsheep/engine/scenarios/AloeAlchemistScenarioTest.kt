package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlotCard
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Aloe Alchemist (OTJ #152) — {1}{G} Plant Warlock, 3/2, Trample, Plot {1}{G}.
 *
 *   "When this card becomes plotted, target creature gets +3/+2 and gains trample until end
 *    of turn."
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.EventPattern.BecomesPlottedEvent] trigger
 * ([com.wingedsheep.sdk.dsl.Triggers.BecomesPlotted]) end-to-end: paying the plot cost exiles
 * the card face up (CR 718) and fires the SELF-bound trigger while the card sits in exile, even
 * though the card is never on the battlefield.
 */
class AloeAlchemistScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("plotting Aloe Alchemist fires its trigger and pumps the chosen creature") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val target = driver.putCreatureOnBattlefield(player, "Grizzly Bears") // 2/2
        val aloe = driver.putCardInHand(player, "Aloe Alchemist")
        driver.giveMana(player, Color.GREEN, 2) // plot cost {1}{G}

        driver.state.projectedState.getPower(target) shouldBe 2
        driver.state.projectedState.getToughness(target) shouldBe 2

        // Plotting pauses for the "becomes plotted" trigger's target choice (CR 603.3d).
        driver.submit(PlotCard(player, aloe)).isPaused shouldBe true
        driver.submitTargetSelection(player, listOf(target))
        driver.bothPass() // resolve the trigger

        // Aloe is now plotted in exile, not on the battlefield.
        driver.getExile(player).contains(aloe) shouldBe true
        driver.getCreatures(player).contains(aloe) shouldBe false

        // Target got +3/+2 and trample until end of turn.
        driver.state.projectedState.getPower(target) shouldBe 5
        driver.state.projectedState.getToughness(target) shouldBe 4
        driver.state.projectedState.hasKeyword(target, Keyword.TRAMPLE) shouldBe true
    }

    test("the pump and trample wear off at end of turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val target = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        val aloe = driver.putCardInHand(player, "Aloe Alchemist")
        driver.giveMana(player, Color.GREEN, 2)

        driver.submit(PlotCard(player, aloe)).isPaused shouldBe true
        driver.submitTargetSelection(player, listOf(target))
        driver.bothPass()

        driver.state.projectedState.getPower(target) shouldBe 5

        // Advance to the next turn — the until-end-of-turn buff is cleaned up.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.state.projectedState.getPower(target) shouldBe 2
        driver.state.projectedState.getToughness(target) shouldBe 2
        driver.state.projectedState.hasKeyword(target, Keyword.TRAMPLE) shouldBe false
    }
})
