package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Academic Dispute (STX) — "Target creature blocks this turn if able. You may have it gain reach
 * until end of turn. Learn."
 *
 * The **Learn** keyword action (CR 701.48a) is the interesting half, and its printed order is
 * load-bearing rather than a "choose one":
 *
 * > "You may discard a card. If you do, draw a card. If you didn't discard a card, you may reveal
 * > a Lesson card you own from outside the game and put it into your hand."
 *
 * — so discarding *forecloses* the Lesson, and only a player who didn't discard is offered the
 * sideboard. Each branch of that sentence gets a test below, plus the two combat clauses.
 *
 * The Lesson used as a fixture is Boomerang Basics (TLA), a real `Sorcery — Lesson` in the corpus.
 */
class AcademicDisputeScenarioTest : ScenarioTestBase() {

    init {
        context("Learn — the discard branch") {
            test("discarding a card draws a card, and the Lesson is never offered") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Academic Dispute")
                    .withCardInHand(1, "Grizzly Bears")   // the card to pitch
                    .withCardInLibrary(1, "Hill Giant")   // the card the draw finds
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withCardInSideboard(1, "Boomerang Basics")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                game.castSpell(1, "Academic Dispute", courser).error shouldBe null
                game.resolveStack()

                // The optional reach rider comes first — decline it.
                game.answerYesNo(false)

                withClue("Learn should offer the up-to-one discard") {
                    game.hasPendingDecision() shouldBe true
                }
                val discardChoice = game.getPendingDecision() as? SelectCardsDecision
                discardChoice.shouldNotBeNull()
                val bears = discardChoice.cardInfo!!.entries
                    .first { it.value.name == "Grizzly Bears" }.key
                game.selectCards(listOf(bears))

                withClue("Discarding is the whole branch — no sideboard prompt follows") {
                    game.hasPendingDecision() shouldBe false
                }
                withClue("Grizzly Bears was discarded") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                withClue("…and the draw replaced it") {
                    game.isInHand(1, "Hill Giant") shouldBe true
                }
                withClue("The Lesson stays in the sideboard — you discarded, so it was never offered") {
                    game.isInSideboard(1, "Boomerang Basics") shouldBe true
                }
            }
        }

        context("Learn — the Lesson branch") {
            test("declining the discard offers a Lesson from outside the game") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Academic Dispute")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withCardInSideboard(1, "Boomerang Basics")  // a Lesson — eligible
                    .withCardInSideboard(1, "Hill Giant")        // not a Lesson — filtered out
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                game.castSpell(1, "Academic Dispute", courser)
                game.resolveStack()
                game.answerYesNo(false)   // decline reach

                game.hasPendingDecision() shouldBe true
                game.skipSelection()      // decline the discard

                withClue("Not discarding should open the sideboard choice") {
                    game.hasPendingDecision() shouldBe true
                }
                val lessonChoice = game.getPendingDecision() as? SelectCardsDecision
                lessonChoice.shouldNotBeNull()
                val offered = lessonChoice.cardInfo!!
                withClue("Only Lesson cards are offered") {
                    offered.values.any { it.name == "Boomerang Basics" } shouldBe true
                    offered.values.any { it.name == "Hill Giant" } shouldBe false
                }

                val lesson = offered.entries.first { it.value.name == "Boomerang Basics" }.key
                game.selectCards(listOf(lesson))

                withClue("The Lesson is now in hand and out of the sideboard") {
                    game.isInHand(1, "Boomerang Basics") shouldBe true
                    game.isInSideboard(1, "Boomerang Basics") shouldBe false
                }
                withClue("Nothing was discarded and nothing was drawn") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                }
            }

            test("declining both halves does nothing at all") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Academic Dispute")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withCardInSideboard(1, "Boomerang Basics")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                game.castSpell(1, "Academic Dispute", courser)
                game.resolveStack()
                game.answerYesNo(false)
                game.skipSelection()   // no discard
                game.skipSelection()   // no Lesson

                game.isInGraveyard(1, "Grizzly Bears") shouldBe false
                game.isInSideboard(1, "Boomerang Basics") shouldBe true
                game.isInHand(1, "Grizzly Bears") shouldBe true
            }

            test("an empty hand skips straight to the Lesson — nothing to discard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Academic Dispute")   // the only card, and it's on the stack
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withCardInSideboard(1, "Boomerang Basics")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                game.castSpell(1, "Academic Dispute", courser)
                game.resolveStack()
                game.answerYesNo(false)

                withClue("With an empty hand the discard auto-resolves to none — this is the Lesson prompt") {
                    game.hasPendingDecision() shouldBe true
                }
                val choice = game.getPendingDecision() as? SelectCardsDecision
                choice.shouldNotBeNull()
                choice.cardInfo!!.values.any { it.name == "Boomerang Basics" } shouldBe true
            }
        }

        context("the combat clauses") {
            test("the targeted creature must block") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Academic Dispute")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(1, "Hill Giant")       // your attacker
                    .withCardOnBattlefield(2, "Centaur Courser")  // the creature you force to block
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                game.castSpell(1, "Academic Dispute", courser)
                game.resolveStack()
                game.answerYesNo(false)   // decline reach
                // Hand and sideboard are both empty here, so Learn raises no prompt at all;
                // decline whatever it does raise rather than assuming a fixed number of them.
                while (game.hasPendingDecision()) game.skipSelection()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Hill Giant" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("Declining to block is illegal while the requirement stands (CR 509.1c)") {
                    game.declareNoBlockers().error shouldNotBe null
                }
            }

            test("the optional rider grants reach to the same creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Academic Dispute")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                game.castSpell(1, "Academic Dispute", courser)
                game.resolveStack()
                game.answerYesNo(true)    // take the reach

                withClue("Centaur Courser should have reach until end of turn") {
                    game.state.projectedState.hasKeyword(courser, com.wingedsheep.sdk.core.Keyword.REACH) shouldBe true
                }
            }
        }
    }
}
