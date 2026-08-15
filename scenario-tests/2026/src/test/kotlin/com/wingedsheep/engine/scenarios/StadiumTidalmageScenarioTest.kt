package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Stadium Tidalmage {2}{U}{R} 4/4 Djinn Sorcerer.
 *
 * "Whenever this creature enters or attacks, you may draw a card. If you do, discard a card."
 *
 * Two separate triggers (enters, attacks) share one optional loot effect. Exercises that:
 *  - the ETB trigger offers the loot and, on accept, draws then discards (net hand size unchanged);
 *  - declining the "may" leaves the hand untouched;
 *  - the attack trigger fires the same loot independently.
 */
class StadiumTidalmageScenarioTest : ScenarioTestBase() {

    init {
        context("Stadium Tidalmage") {

            test("enters trigger: accepting the may loots (draw then discard) — net hand size unchanged") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Stadium Tidalmage")
                    .withCardInHand(1, "Grizzly Bears") // a card to discard
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Plains") // a card to draw
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Stadium Tidalmage").error shouldBe null
                game.resolveStack()

                withClue("the enters trigger pends a may-loot decision") {
                    (game.state.pendingDecision != null) shouldBe true
                }
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                withClue("drew Plains") {
                    game.isInHand(1, "Plains") shouldBe true
                }
                // discard the original Grizzly Bears to satisfy the loot
                val bears = game.state.getZone(
                    com.wingedsheep.engine.state.ZoneKey(game.player1Id, com.wingedsheep.sdk.core.Zone.HAND)
                ).first {
                    game.state.getEntity(it)
                        ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Grizzly Bears"
                }
                game.selectCards(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears was discarded") {
                    game.isInHand(1, "Grizzly Bears") shouldBe false
                }
            }

            test("enters trigger: declining the may leaves the hand untouched") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Stadium Tidalmage")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Stadium Tidalmage").error shouldBe null
                game.resolveStack()

                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("declining draws nothing and discards nothing") {
                    game.isInHand(1, "Plains") shouldBe false
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
            }

            test("attack trigger: declaring an attack offers the same may-loot") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Stadium Tidalmage", summoningSickness = false)
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Stadium Tidalmage" to 2)).error shouldBe null
                game.resolveStack()

                withClue("attacking fires the loot may-decision") {
                    (game.state.pendingDecision != null) shouldBe true
                }
            }
        }
    }
}
