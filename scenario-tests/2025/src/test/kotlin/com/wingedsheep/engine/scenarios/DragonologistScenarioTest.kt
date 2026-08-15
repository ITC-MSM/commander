package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dragonologist (TDM #42) — {2}{U} Human Wizard, 1/3.
 *
 * "When this creature enters, look at the top six cards of your library. You may reveal an
 *  instant, sorcery, or Dragon card from among them and put it into your hand. Put the rest on
 *  the bottom of your library in a random order."
 * "Untapped Dragons you control have hexproof."
 */
class DragonologistScenarioTest : ScenarioTestBase() {

    init {
        context("Dragonologist enters-the-battlefield dig") {

            test("reveals an instant from the top six and puts it into hand, bottoming the rest") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragonologist")
                    .withLandsOnBattlefield(1, "Island", 3)
                    // Six cards on top: one instant (eligible) among five lands (ineligible).
                    .withCardInLibrary(1, "Rebellious Strike") // instant — eligible
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val strike = game.findCardsInLibrary(1, "Rebellious Strike").first()

                game.castSpell(1, "Dragonologist").error shouldBe null
                game.resolveStack() // creature enters → ETB look-six pauses for a selection

                game.getPendingDecision() ?: error("expected a selection decision for the look-six trigger")
                game.selectCards(listOf(strike))
                game.resolveStack()

                withClue("The revealed instant is now in hand") {
                    game.findCardsInHand(1, "Rebellious Strike").size shouldBe 1
                }
                withClue("The other five looked-at cards are on the bottom of the library (none kept)") {
                    game.findCardsInLibrary(1, "Rebellious Strike").size shouldBe 0
                }
                withClue("Dragonologist resolves onto the battlefield") {
                    game.isOnBattlefield("Dragonologist") shouldBe true
                }
            }

            test("may decline to keep any of the looked-at cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dragonologist")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Dragonologist").error shouldBe null
                game.resolveStack()

                // Only lands are visible (ineligible) — decline by selecting nothing.
                game.getPendingDecision() ?: error("expected a selection decision for the look-six trigger")
                game.selectCards(emptyList())
                game.resolveStack()

                withClue("Nothing was put into hand") {
                    game.findCardsInHand(1, "Island").size shouldBe 0
                }
                withClue("Dragonologist resolves onto the battlefield") {
                    game.isOnBattlefield("Dragonologist") shouldBe true
                }
            }
        }

        context("Dragonologist static — untapped Dragons you control have hexproof") {

            test("an untapped Dragon you control has hexproof, a tapped one does not") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dragonologist")
                    .withCardOnBattlefield(1, "Kilnmouth Dragon", tapped = false)
                    .withCardOnBattlefield(2, "Kilnmouth Dragon", tapped = false) // opponent's Dragon
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val myDragon = game.findPermanents("Kilnmouth Dragon")
                    .first { game.state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.ControllerComponent>()?.playerId == game.player1Id }
                val theirDragon = game.findPermanents("Kilnmouth Dragon")
                    .first { game.state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.ControllerComponent>()?.playerId == game.player2Id }

                withClue("Untapped Dragon you control gains hexproof from Dragonologist") {
                    game.state.projectedState.hasKeyword(myDragon, Keyword.HEXPROOF) shouldBe true
                }
                withClue("Opponent's Dragon is not affected (only Dragons you control)") {
                    game.state.projectedState.hasKeyword(theirDragon, Keyword.HEXPROOF) shouldBe false
                }
            }

            test("a tapped Dragon you control does not have hexproof") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dragonologist")
                    .withCardOnBattlefield(1, "Kilnmouth Dragon", tapped = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tappedDragon = game.findPermanent("Kilnmouth Dragon")!!
                withClue("Tapped Dragon does not have hexproof (static only grants to untapped Dragons)") {
                    game.state.projectedState.hasKeyword(tappedDragon, Keyword.HEXPROOF) shouldBe false
                }
            }
        }
    }
}
