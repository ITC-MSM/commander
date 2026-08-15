package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Monastery Messenger (TDM #208) — {2/U}{2/R}{2/W} Bird Scout, 2/3,
 * Flying, vigilance.
 *
 * "When this creature enters, put up to one target noncreature, nonland card from your
 *  graveyard on top of your library."
 *
 * Exercises the optional (up to one) graveyard target restricted to noncreature, nonland
 * cards, moved to the top of the owner's library.
 */
class MonasteryMessengerScenarioTest : ScenarioTestBase() {

    init {
        context("Monastery Messenger enters-the-battlefield trigger") {

            test("puts a noncreature, nonland card from your graveyard on top of your library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Monastery Messenger")
                    .withLandsOnBattlefield(1, "Island", 6) // pay the twobrid cost as generic
                    .withCardInGraveyard(1, "Rebellious Strike") // an instant — a legal target
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Monastery Messenger").error shouldBe null
                game.resolveStack() // creature enters → ETB trigger on stack, asks for a target

                val strike = game.findCardsInGraveyard(1, "Rebellious Strike").first()
                val result = game.selectTargets(listOf(strike))
                withClue("An instant in your graveyard is a legal target: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Rebellious Strike leaves the graveyard") {
                    game.findCardsInGraveyard(1, "Rebellious Strike").size shouldBe 0
                }
                withClue("Rebellious Strike is on top of the library") {
                    game.state.getLibrary(game.player1Id).first() shouldBe strike
                }
                withClue("Monastery Messenger resolves onto the battlefield") {
                    game.isOnBattlefield("Monastery Messenger") shouldBe true
                }
            }

            test("may decline (up to one) when only ineligible cards are present") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Monastery Messenger")
                    .withLandsOnBattlefield(1, "Island", 6)
                    .withCardInGraveyard(1, "Grizzly Bears") // a creature — not a legal target
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Monastery Messenger").error shouldBe null
                game.resolveStack()

                // "up to one" — decline by selecting nothing.
                game.skipTargets().error shouldBe null
                game.resolveStack()

                withClue("The creature card stays in the graveyard (never a legal target)") {
                    game.findCardsInGraveyard(1, "Grizzly Bears").size shouldBe 1
                }
                withClue("Monastery Messenger resolves onto the battlefield") {
                    game.isOnBattlefield("Monastery Messenger") shouldBe true
                }
            }
        }
    }
}
