package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Witherbloom Charm (Secrets of Strixhaven #244).
 *
 * Witherbloom Charm ({B}{G} Instant), Choose one —
 *   • You may sacrifice a permanent. If you do, draw two cards.
 *   • You gain 5 life.
 *   • Destroy target nonland permanent with mana value 2 or less.
 */
class WitherbloomCharmScenarioTest : ScenarioTestBase() {

    init {
        context("Witherbloom Charm — choose one") {

            test("mode 1: sacrifice a permanent, then draw two cards") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Witherbloom Charm")
                    .withCardOnBattlefield(1, "Grizzly Bears")     // sacrifice fodder
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpellWithMode(1, "Witherbloom Charm", modeIndex = 0).error shouldBe null
                game.resolveStack()

                // "You may" -> yes, then choose the permanent to sacrifice.
                if (game.hasPendingDecision()) game.answerYesNo(true)
                if (game.hasPendingDecision()) game.selectCards(listOf(bears))
                game.resolveStack()

                withClue("Grizzly Bears was sacrificed") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }
                // hand: -1 (the charm) + 2 (drawn) = handBefore + 1
                withClue("drew two cards after sacrificing") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("mode 1 declined: no sacrifice, no draw") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Witherbloom Charm")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                game.castSpellWithMode(1, "Witherbloom Charm", modeIndex = 0).error shouldBe null
                game.resolveStack()
                if (game.hasPendingDecision()) game.answerYesNo(false)
                game.resolveStack()

                withClue("Grizzly Bears survives — declined the sacrifice") {
                    (game.findPermanent("Grizzly Bears") != null) shouldBe true
                }
                withClue("no draw — only the charm left hand") {
                    game.handSize(1) shouldBe handBefore - 1
                }
            }

            test("mode 2: gain 5 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Witherbloom Charm")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellWithMode(1, "Witherbloom Charm", modeIndex = 1).error shouldBe null
                game.resolveStack()

                withClue("gained 5 life") { game.getLifeTotal(1) shouldBe 25 }
            }

            test("mode 3: destroy target nonland permanent with mana value 2 or less") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Witherbloom Charm")
                    .withCardOnBattlefield(2, "Grizzly Bears")     // MV 2 — legal
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpellWithMode(1, "Witherbloom Charm", modeIndex = 2, targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears (MV 2) was destroyed") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }

            test("mode 3: a mana value 3+ permanent is not a legal target") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Witherbloom Charm")
                    .withCardOnBattlefield(2, "Hill Giant")        // MV 4 — illegal
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                val result = game.castSpellWithMode(1, "Witherbloom Charm", modeIndex = 2, targetId = giant)

                withClue("Hill Giant (MV 4) is not a legal target for the destroy mode") {
                    (result.error != null) shouldBe true
                }
            }
        }
    }
}
