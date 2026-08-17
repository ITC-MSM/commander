package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.RakdosPatronOfChaos
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Rakdos, Patron of Chaos (MKM #224) — {4}{B}{R} 6/6 Legendary Demon, flying and trample.
 *
 * "At the beginning of your end step, target opponent may sacrifice two nonland, nontoken permanents
 *  of their choice. If they don't, you draw two cards."
 *
 * The whole card is the punisher branch, and the branch has three distinct outcomes that a naive
 * implementation gets wrong in three different ways:
 *
 *  - **taking the sacrifice** must cost the *opponent* two permanents and draw the controller nothing
 *    (a `Gate.MayPay` would have sacrificed the controller's permanents instead);
 *  - **declining** must draw the controller exactly two;
 *  - **being unable to sacrifice two** must fall through to the draw with no prompt at all. This is
 *    the one that matters most: feasibility has to be judged against the *choosing* player, and the
 *    force-sacrifice must not settle for one permanent when the card demands two.
 *
 * The first test also pins the "nonland" half of the filter: a land the opponent controls is never an
 * eligible sacrifice, and must survive.
 */
class RakdosPatronOfChaosScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + RakdosPatronOfChaos)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Walk to the end step and resolve the trigger far enough to reach the opponent's choice —
     * or, when the sacrifice option is infeasible, past it entirely. Targeting the sole opponent
     * may or may not raise an explicit decision, so both paths are handled.
     */
    fun GameTestDriver.resolveEndStepTrigger(controller: EntityId, opponent: EntityId) {
        passPriorityUntil(Step.END)
        var guard = 0
        while (guard++ < 16) {
            when (pendingDecision) {
                is ChooseTargetsDecision -> submitTargetSelection(controller, listOf(opponent))
                is ChooseOptionDecision -> return
                else -> if (stackSize > 0 || priorityPlayer != null) bothPass() else return
            }
        }
    }

    fun GameTestDriver.chooseOption(player: EntityId, needle: String) {
        val decision = pendingDecision as ChooseOptionDecision
        val index = decision.options.indexOfFirst { it.contains(needle, ignoreCase = true) }
        withClue("the '$needle' option was offered (saw ${decision.options})") { (index >= 0) shouldBe true }
        submitDecision(player, OptionChosenResponse(decision.id, index)).error shouldBe null
    }

    test("the opponent may sacrifice two nonland, nontoken permanents — and the controller draws nothing") {
        val driver = createDriver()
        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)

        driver.putCreatureOnBattlefield(controller, "Rakdos, Patron of Chaos")
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.putCreatureOnBattlefield(opponent, "Minotaur Warrior")
        // A third eligible body, so the opponent gets a genuine choice rather than an
        // auto-sacrifice of everything they have — which is what pins the filter.
        driver.putCreatureOnBattlefield(opponent, "Savannah Lions")
        // A land the opponent controls must never be an eligible sacrifice.
        driver.putLandOnBattlefield(opponent, "Swamp")

        val handBefore = driver.getHandSize(controller)
        val creaturesBefore = driver.getCreatures(opponent).size
        driver.resolveEndStepTrigger(controller, opponent)
        driver.chooseOption(opponent, "Sacrifice")

        val selection = driver.pendingDecision as SelectCardsDecision
        withClue("only the three creatures are eligible — the land is filtered out") {
            selection.options.mapNotNull { driver.getCardName(it) }.sorted() shouldBe
                listOf("Grizzly Bears", "Minotaur Warrior", "Savannah Lions")
        }
        withClue("the card demands exactly two") {
            selection.minSelections shouldBe 2
            selection.maxSelections shouldBe 2
        }
        driver.submitCardSelection(opponent, selection.options.take(2)).error shouldBe null
        repeat(4) { if (driver.stackSize > 0 || driver.pendingDecision != null) driver.bothPass() }

        withClue("exactly two creatures were sacrificed") {
            driver.getCreatures(opponent).size shouldBe creaturesBefore - 2
        }
        withClue("the land survived") {
            driver.getLands(opponent).size shouldBe 1
        }
        withClue("they did sacrifice, so the controller draws nothing") {
            driver.getHandSize(controller) shouldBe handBefore
        }
    }

    test("declining draws the controller two cards") {
        val driver = createDriver()
        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)

        driver.putCreatureOnBattlefield(controller, "Rakdos, Patron of Chaos")
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.putCreatureOnBattlefield(opponent, "Minotaur Warrior")

        val handBefore = driver.getHandSize(controller)
        driver.resolveEndStepTrigger(controller, opponent)
        driver.chooseOption(opponent, "draw")
        repeat(4) { if (driver.stackSize > 0 || driver.pendingDecision != null) driver.bothPass() }

        withClue("nothing was sacrificed") {
            driver.getCreatures(opponent).size shouldBe 2
        }
        withClue("'if they don't' fired — exactly two cards") {
            driver.getHandSize(controller) shouldBe handBefore + 2
        }
    }

    test("an opponent who can't find two legal permanents is never asked, and the controller draws") {
        val driver = createDriver()
        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)

        driver.putCreatureOnBattlefield(controller, "Rakdos, Patron of Chaos")
        // One legal permanent plus lands — which never add up to the two the card demands.
        val lone = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        driver.putLandOnBattlefield(opponent, "Swamp")
        driver.putLandOnBattlefield(opponent, "Swamp")

        val handBefore = driver.getHandSize(controller)
        driver.resolveEndStepTrigger(controller, opponent)

        withClue("the sacrifice option was infeasible, so no choice was offered") {
            (driver.pendingDecision is ChooseOptionDecision) shouldBe false
        }
        repeat(4) { if (driver.stackSize > 0 || driver.pendingDecision != null) driver.bothPass() }

        withClue("the single permanent must NOT have been eaten — the card demands two") {
            driver.findPermanent(opponent, "Grizzly Bears") shouldBe lone
        }
        withClue("they couldn't sacrifice, so they didn't, so the controller draws two") {
            driver.getHandSize(controller) shouldBe handBefore + 2
        }
    }
})
