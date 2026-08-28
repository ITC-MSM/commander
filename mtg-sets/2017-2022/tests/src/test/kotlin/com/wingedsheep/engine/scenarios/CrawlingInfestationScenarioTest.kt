package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Crawling Infestation (VOW #193) — {2}{G} Enchantment.
 *
 *   At the beginning of your upkeep, you may mill two cards.
 *   Whenever one or more creature cards are put into your graveyard from anywhere during your
 *   turn, create a 1/1 green Insect creature token. This ability triggers only once each turn.
 *
 * The two riders on the second ability are separate knobs and each gets its own test: "during
 * your turn" (`triggerRestriction`) and "only once each turn" (`oncePerTurn`). Both are the kind
 * of axis a code path can silently ignore, which is why they are asserted rather than assumed.
 */
class CrawlingInfestationScenarioTest : ScenarioTestBase() {

    init {
        context("Crawling Infestation") {

            test("the upkeep mill is optional and feeds the Insect trigger") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Crawling Infestation")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                val librarySizeBefore = game.librarySize(1)

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("the upkeep trigger asks whether to mill") {
                    game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                }
                game.answerYesNo(true)
                game.resolveStack()

                withClue("exactly two cards are milled") {
                    game.librarySize(1) shouldBe librarySizeBefore - 2
                }
                withClue("two creature cards hitting the graveyard makes one Insect, not two") {
                    game.findPermanents("Insect Token").size shouldBe 1
                }
            }

            test("declining the upkeep mill mills nothing and makes no Insect") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Crawling Infestation")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                val librarySizeBefore = game.librarySize(1)

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("nothing is milled") {
                    game.librarySize(1) shouldBe librarySizeBefore
                }
                withClue("no Insect token is created") {
                    game.findPermanents("Insect Token").size shouldBe 0
                }
            }

            test("the ability triggers only once each turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Crawling Infestation")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)
                game.resolveStack()

                withClue("the mill makes the turn's one Insect") {
                    game.findPermanents("Insect Token").size shouldBe 1
                }

                game.advanceToPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Doom Blade", bears).error shouldBe null
                game.resolveStack()

                withClue("the Grizzly Bears died into our graveyard on our own turn") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("the turn's single trigger was already spent — still one Insect") {
                    game.findPermanents("Insect Token").size shouldBe 1
                }
            }

            test("a creature card reaching your graveyard on an opponent's turn makes no Insect") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Crawling Infestation")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(2, "Doom Blade")
                    .withLandsOnBattlefield(2, "Swamp", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(2, "Doom Blade", bears).error shouldBe null
                game.resolveStack()

                withClue("the creature card is in our graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("but it wasn't our turn, so no Insect") {
                    game.findPermanents("Insect Token").size shouldBe 0
                }
            }
        }
    }
}
