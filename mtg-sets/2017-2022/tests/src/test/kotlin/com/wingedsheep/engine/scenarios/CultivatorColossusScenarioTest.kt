package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Cultivator Colossus (VOW #195) — {4}{G}{G}{G} Creature — Plant Beast, star/star.
 *
 *   Trample
 *   Cultivator Colossus's power and toughness are each equal to the number of lands you control.
 *   When this creature enters, you may put a land card from your hand onto the battlefield tapped.
 *   If you do, draw a card and repeat this process.
 *
 * The characteristic-defining P/T reads the controller's land count. The ETB is a do-while loop:
 * each pass may put one land from hand tapped and — only if a land was put — draws a card, then
 * repeats. Declining (selecting no land) ends the loop.
 */
class CultivatorColossusScenarioTest : ScenarioTestBase() {

    /**
     * Drive the ETB land-put loop: on each [SelectCardsDecision], put one land if [remainingPuts]
     * are left (choosing the first option), otherwise decline (select none). Stops when the loop
     * ends and the stack drains.
     */
    private fun driveLandLoop(game: TestGame, remainingPuts: Int) {
        var puts = remainingPuts
        var guard = 0
        while (game.hasPendingDecision() && guard++ < 40) {
            val decision = game.getPendingDecision()
            if (decision is SelectCardsDecision) {
                if (puts > 0 && decision.options.isNotEmpty()) {
                    puts--
                    game.selectCards(listOf(decision.options.first()))
                } else {
                    game.skipSelection()
                }
                game.resolveStack()
            } else {
                break
            }
        }
    }

    init {
        context("Cultivator Colossus") {

            test("P/T equals the number of lands you control, and it has trample") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cultivator Colossus", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val colossus = game.findPermanent("Cultivator Colossus")!!
                withClue("power/toughness equal the 5 lands you control") {
                    game.state.projectedState.getPower(colossus) shouldBe 5
                    game.state.projectedState.getToughness(colossus) shouldBe 5
                }
                withClue("Cultivator Colossus has trample") {
                    game.state.projectedState.hasKeyword(colossus, Keyword.TRAMPLE) shouldBe true
                }
            }

            test("ETB: putting two lands tapped draws two cards and grows the Colossus") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cultivator Colossus")
                    .withLandsOnBattlefield(1, "Forest", 7) // pays {4}{G}{G}{G}
                    .withCardInHand(1, "Forest")
                    .withCardInHand(1, "Forest")
                    // Two library cards to draw from as the loop puts lands.
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handLandsBefore = game.findCardsInHand(1, "Forest").size
                withClue("two Forest cards start in hand") { handLandsBefore shouldBe 2 }
                val libraryBefore = game.librarySize(1)

                game.castSpell(1, "Cultivator Colossus").error shouldBe null
                game.resolveStack() // enters → ETB loop begins asking to put a land

                // Put both Forests (two passes), then the third pass has no land to put → decline.
                driveLandLoop(game, remainingPuts = 2)

                val colossus = game.findPermanent("Cultivator Colossus")!!
                withClue("Cultivator Colossus resolved onto the battlefield") {
                    colossus shouldBe colossus
                }
                withClue("both Forests left the hand onto the battlefield") {
                    game.findCardsInHand(1, "Forest").size shouldBe 0
                }
                withClue("two lands entered, so two cards were drawn") {
                    (libraryBefore - game.librarySize(1)) shouldBe 2
                }
                // 7 starting Forests + 2 put from hand = 9 lands you control.
                withClue("P/T now reflect the 9 lands you control") {
                    game.state.projectedState.getPower(colossus) shouldBe 9
                    game.state.projectedState.getToughness(colossus) shouldBe 9
                }
            }

            test("ETB with no lands in hand: the loop ends immediately, no draw") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cultivator Colossus")
                    .withLandsOnBattlefield(1, "Forest", 7)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val libraryBefore = game.librarySize(1)

                game.castSpell(1, "Cultivator Colossus").error shouldBe null
                game.resolveStack()
                // Empty-hand loop: if it pauses at all, decline; otherwise it already ended.
                driveLandLoop(game, remainingPuts = 0)

                withClue("no land in hand → no card drawn") {
                    (libraryBefore - game.librarySize(1)) shouldBe 0
                }
                withClue("Cultivator Colossus is on the battlefield") {
                    game.isOnBattlefield("Cultivator Colossus") shouldBe true
                }
            }
        }
    }
}
