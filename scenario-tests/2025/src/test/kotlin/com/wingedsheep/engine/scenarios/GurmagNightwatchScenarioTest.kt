package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Gurmag Nightwatch (TDM) — {2/B}{2/G}{2/U} Creature — Human Ranger, 3/3.
 *
 * "When this creature enters, look at the top three cards of your library. You may put one of
 *  those cards back on top of your library. Put the rest into your graveyard."
 *
 * Exercises the look-at-top / select-one / bin-the-rest pipeline: of the three cards looked at,
 * the chosen one stays on top of the library and the other two go to the graveyard.
 */
class GurmagNightwatchScenarioTest : ScenarioTestBase() {

    init {
        context("Gurmag Nightwatch enters-the-battlefield trigger") {

            test("keeps one of the top three on top and bins the rest") {
                // 6 lands pays the twobrid cost as generic. Three known cards seed the top of the library.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Gurmag Nightwatch")
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Hill Giant")
                    .withCardInLibrary(1, "Bog Imp")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val libraryBefore = game.librarySize(1)
                val topThree = game.state.getLibrary(game.player1Id).take(3)
                val keep = topThree.first()

                val cast = game.castSpell(1, "Gurmag Nightwatch")
                withClue("Cast should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // ETB look-3 pauses for a "choose up to one to keep on top" selection.
                game.getPendingDecision() ?: error("expected a selection decision for the look-three trigger")
                game.selectCards(listOf(keep))
                game.resolveStack()

                withClue("Two of the three looked-at cards are binned to the graveyard") {
                    game.graveyardSize(1) shouldBe 2
                }
                withClue("Net library loss is three (the three looked at), one of which returns to top") {
                    game.librarySize(1) shouldBe libraryBefore - 2
                }
                withClue("The kept card is back on top of the library") {
                    game.state.getLibrary(game.player1Id).first() shouldBe keep
                }
                withClue("Gurmag Nightwatch resolves onto the battlefield") {
                    game.isOnBattlefield("Gurmag Nightwatch") shouldBe true
                }
            }
        }
    }
}
