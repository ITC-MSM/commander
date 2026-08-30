package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Consuming Tide (VOW #53) — {2}{U}{U} Sorcery.
 *
 *   Each player chooses a nonland permanent they control. Return all nonland permanents not chosen
 *   this way to their owners' hands. Then you draw a card for each opponent who has more cards in
 *   their hand than you.
 *
 * Both players hold two nonland permanents; the active player picks first (APNAP), each keeps one,
 * and the other two go back to their owners' hands. The draw is counted *after* the bounce.
 */
class ConsumingTideScenarioTest : ScenarioTestBase() {

    init {
        context("Consuming Tide") {

            test("each player keeps one, the rest bounce to their owners, then you draw per richer opponent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Consuming Tide")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withCardOnBattlefield(2, "Serra Angel")
                    .withCardOnBattlefield(2, "Wind Drake")
                    .withCardInHand(2, "Grizzly Bears")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val angel = game.findPermanent("Serra Angel")!!

                game.castSpell(1, "Consuming Tide").error shouldBe null
                game.resolveStack()

                withClue("the caster (active player) chooses first") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectCards(listOf(bears)).error shouldBe null
                game.resolveStack()
                withClue("then the opponent chooses") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectCards(listOf(angel)).error shouldBe null
                game.resolveStack()

                withClue("kept permanents stay") {
                    game.findPermanent("Grizzly Bears") shouldBe bears
                    game.findPermanent("Serra Angel") shouldBe angel
                }
                withClue("the rest return to their owners' hands") {
                    game.findPermanent("Hill Giant") shouldBe null
                    game.findPermanent("Wind Drake") shouldBe null
                    game.findCardsInHand(1, "Hill Giant").size shouldBe 1
                    game.findCardsInHand(2, "Wind Drake").size shouldBe 1
                }
                withClue("lands are untouched") {
                    game.findPermanents("Island").size shouldBe 4
                }
                // Post-bounce hands: P1 = Hill Giant (1); P2 = Grizzly Bears + Wind Drake (2).
                // One opponent with more cards than you → draw one → P1 hand = 2.
                withClue("you draw one card for the single richer opponent") {
                    game.handSize(2) shouldBe 2
                    game.handSize(1) shouldBe 2
                }
            }

            test("no opponent with a bigger hand — no draw") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Consuming Tide")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Serra Angel")
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Consuming Tide").error shouldBe null
                game.resolveStack()
                withClue("a single candidate per player auto-resolves without a prompt") {
                    game.hasPendingDecision() shouldBe false
                }

                game.findPermanents("Grizzly Bears").size shouldBe 1
                game.findPermanents("Serra Angel").size shouldBe 1
                withClue("hands are 0 vs 0 — nothing drawn") {
                    game.handSize(1) shouldBe 0
                    game.handSize(2) shouldBe 0
                }
            }
        }
    }
}
