package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Sleight of Hand — {U} Sorcery — "Look at the top two cards of your library. Put one of them into
 * your hand and the other on the bottom of your library."
 *
 * Canonical script lives in P02; WOE is a reprint row. The library holds exactly two cards so both
 * are the "top two"; the chosen card goes to hand and the other stays in the library (net −1).
 */
class SleightOfHandScenarioTest : ScenarioTestBase() {

    init {
        test("keeps one of the top two and bottoms the other") {
            val g = scenario()
                .withPlayers()
                .withCardInHand(1, "Sleight of Hand")
                .withLandsOnBattlefield(1, "Island", 1)
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Centaur Courser")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withActivePlayer(1)
                .withPriorityPlayer(1)
                .build()

            g.librarySize(1) shouldBe 2
            // Capture the ids while both are still in the library; the "look" moves them into a
            // temporary collection, so we can't re-find them by zone mid-resolution.
            val bears = g.findCardsInLibrary(1, "Grizzly Bears").first()

            g.castSpell(1, "Sleight of Hand").error shouldBe null
            g.resolveStack()

            val decision = g.getPendingDecision()
            withClue("look at the top two: a selection over both cards") {
                (decision is SelectCardsDecision) shouldBe true
                (decision as SelectCardsDecision).options.size shouldBe 2
            }

            g.selectCards(listOf(bears))
            g.resolveStack()

            withClue("chosen card is in hand") {
                g.isInHand(1, "Grizzly Bears") shouldBe true
            }
            withClue("the other card stayed in the library (bottomed), net −1") {
                g.librarySize(1) shouldBe 1
                g.findCardsInLibrary(1, "Centaur Courser").size shouldBe 1
            }
        }
    }
}
