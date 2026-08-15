package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Stockpiling Celebrant — {2}{W} Creature — Dwarf Knight 3/2 — "When this creature enters, you may
 * return another target nonland permanent you control to its owner's hand. If you do, scry 2."
 *
 * Accepting the "may" bounces the chosen permanent to hand and then scrys 2. Declining does neither.
 */
class StockpilingCelebrantScenarioTest : ScenarioTestBase() {

    private fun game() = scenario()
        .withPlayers()
        .withCardInHand(1, "Stockpiling Celebrant")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withLandsOnBattlefield(1, "Plains", 3)
        .withCardInLibrary(1, "Forest")
        .withCardInLibrary(1, "Forest")
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .withActivePlayer(1)
        .withPriorityPlayer(1)
        .build()

    init {
        test("accepting returns a permanent to hand and scrys 2") {
            val g = game()
            val bears = g.findPermanent("Grizzly Bears")!!

            g.castSpell(1, "Stockpiling Celebrant").error shouldBe null

            var sawScry = false
            var guard = 0
            g.resolveStack()
            while (g.getPendingDecision() != null && guard++ < 12) {
                when (g.getPendingDecision()) {
                    is YesNoDecision -> g.answerYesNo(true)          // accept the "may"
                    is ChooseTargetsDecision -> g.selectTargets(listOf(bears))
                    is SelectCardsDecision -> { sawScry = true; g.skipSelection() } // scry: keep on top
                    is ReorderLibraryDecision -> g.keepLibraryOrder()
                    else -> break
                }
                g.resolveStack()
            }

            withClue("Stockpiling Celebrant resolved onto the battlefield") {
                g.isOnBattlefield("Stockpiling Celebrant") shouldBe true
            }
            withClue("the targeted permanent was returned to its owner's hand") {
                g.isOnBattlefield("Grizzly Bears") shouldBe false
                g.isInHand(1, "Grizzly Bears") shouldBe true
            }
            withClue("scry 2 happened because the permanent was returned") {
                sawScry shouldBe true
            }
        }

        test("declining leaves everything in place") {
            val g = game()

            g.castSpell(1, "Stockpiling Celebrant").error shouldBe null

            var sawScry = false
            var guard = 0
            g.resolveStack()
            while (g.getPendingDecision() != null && guard++ < 12) {
                when (g.getPendingDecision()) {
                    is YesNoDecision -> g.answerYesNo(false)         // decline the "may"
                    is SelectCardsDecision -> { sawScry = true; g.skipSelection() }
                    is ReorderLibraryDecision -> g.keepLibraryOrder()
                    else -> break
                }
                g.resolveStack()
            }

            withClue("declining leaves the creature and skips bounce + scry") {
                g.isOnBattlefield("Stockpiling Celebrant") shouldBe true
                g.isOnBattlefield("Grizzly Bears") shouldBe true
                sawScry shouldBe false
            }
        }
    }
}
