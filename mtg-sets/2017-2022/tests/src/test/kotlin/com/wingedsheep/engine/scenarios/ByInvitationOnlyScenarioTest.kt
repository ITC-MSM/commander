package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for By Invitation Only (VOW #5) — {3}{W}{W} Sorcery.
 *
 *   Choose a number between 0 and 13. Each player sacrifices that many creatures of their choice.
 *
 * The card is Blasphemous Edict with the count taken from a resolution-time number decision, so
 * what these tests actually prove is that the chosen number reaches the sacrifice: that the X
 * `ChooseNumberThenEffect` stamps onto the resolution context is what `DynamicAmount.XValue` reads
 * back inside the inner effect, once, for every player.
 *
 * Both edges of the range are covered because they are the two ways the number could fail to
 * arrive: 0 has to sacrifice nothing rather than default to 1, and a number larger than a player's
 * board has to take everything rather than error (second ruling).
 */
class ByInvitationOnlyScenarioTest : ScenarioTestBase() {

    init {
        context("By Invitation Only") {

            test("the chosen number is how many creatures each player sacrifices") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "By Invitation Only")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val card = game.findCardsInHand(1, "By Invitation Only").first()
                game.execute(CastSpell(game.player1Id, card, emptyList())).error shouldBe null
                game.resolveStack()

                game.chooseNumber(1).error shouldBe null

                // Each player in turn order picks their own one creature to sacrifice.
                repeat(2) {
                    val decision = game.getPendingDecision() as? SelectCardsDecision
                    val pick = decision?.options?.firstOrNull()
                    if (pick != null) game.selectCards(listOf(pick)).error shouldBe null
                }
                game.resolveStack()

                withClue("each player sacrificed exactly one of their two creatures") {
                    game.findAllPermanents("Grizzly Bears").size +
                        game.findAllPermanents("Hill Giant").size shouldBe 2
                }
                withClue("one creature reached each graveyard") {
                    game.graveyardSize(1) shouldBe 2 // the sacrificed creature + the sorcery
                    game.graveyardSize(2) shouldBe 1
                }
            }

            test("choosing 0 sacrifices nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "By Invitation Only")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val card = game.findCardsInHand(1, "By Invitation Only").first()
                game.execute(CastSpell(game.player1Id, card, emptyList())).error shouldBe null
                game.resolveStack()

                game.chooseNumber(0).error shouldBe null
                game.resolveStack()

                withClue("0 is a legal choice and takes nothing (first ruling)") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
            }

            test("a player with fewer creatures than the number sacrifices all of them") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "By Invitation Only")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val card = game.findCardsInHand(1, "By Invitation Only").first()
                game.execute(CastSpell(game.player1Id, card, emptyList())).error shouldBe null
                game.resolveStack()

                game.chooseNumber(13).error shouldBe null
                repeat(4) {
                    val available = (game.getPendingDecision() as? SelectCardsDecision)
                        ?.options.orEmpty()
                    if (available.isNotEmpty()) game.selectCards(available).error shouldBe null
                }
                game.resolveStack()

                withClue("nobody had 13 creatures, so everybody lost the ones they had") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isOnBattlefield("Hill Giant") shouldBe false
                }
            }
        }
    }
}
