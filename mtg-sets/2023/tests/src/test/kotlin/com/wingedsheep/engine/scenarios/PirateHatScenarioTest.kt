package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Pirate Hat (LCI #70) — {1}{U} Artifact — Equipment.
 *
 * Equipped creature gets +1/+1 and has "Whenever this creature attacks, draw a card,
 * then discard a card."
 * Equip Pirate {1}
 * Equip {2}
 *
 * Tests:
 *  1. Equipped creature gets +1/+1 from the static ability.
 *  2. Attacking with the equipped creature loots (draw a card, then discard a card).
 *  3. An unequipped creature attacking does NOT loot (the grant is inactive).
 */
class PirateHatScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Pirate Hat") {

            test("equipped creature gets +1/+1") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Pirate Hat", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = projector.project(game.state)

                withClue("Grizzly Bears base 2/2 + Pirate Hat +1/+1 = 3/3") {
                    projected.getPower(bears) shouldBe 3
                    projected.getToughness(bears) shouldBe 3
                }
            }

            test("attacking with the equipped creature loots (draw, then discard)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Pirate Hat", "Grizzly Bears")
                    .withCardInHand(1, "Grizzly Bears") // a card to discard
                    .withCardInLibrary(1, "Grizzly Bears") // a card to draw
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val libBefore = game.state.getLibrary(game.player1Id).size
                val gyBefore = game.state.getGraveyard(game.player1Id).size

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("Pirate Hat's attack trigger drew a card (library -1)") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore - 1
                }

                // The loot's discard step presents a card-selection decision; choose one to discard.
                val discard = game.getPendingDecision()
                withClue("loot presents a discard selection over the hand") {
                    (discard is SelectCardsDecision) shouldBe true
                }
                game.selectCards(listOf((discard as SelectCardsDecision).options.first())).error shouldBe null
                game.resolveStack()

                withClue("Pirate Hat's attack trigger discarded a card (graveyard +1)") {
                    game.state.getGraveyard(game.player1Id).size shouldBe gyBefore + 1
                }
            }

            test("unequipped creature attacking does NOT loot") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Pirate Hat")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val libBefore = game.state.getLibrary(game.player1Id).size

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("No loot — Pirate Hat is not equipped, grant is inactive") {
                    game.state.getLibrary(game.player1Id).size shouldBe libBefore
                    game.hasPendingDecision() shouldBe false
                }
            }
        }
    }
}
