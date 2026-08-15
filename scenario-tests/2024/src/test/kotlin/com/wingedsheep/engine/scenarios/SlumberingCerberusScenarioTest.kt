package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Slumbering Cerberus (FDN #94) — {1}{R} 4/2 Creature — Dog
 *
 * "This creature doesn't untap during your untap step.
 *  Morbid — At the beginning of each end step, if a creature died this turn, untap this creature."
 *
 * Verifies:
 *  (a) it stays tapped through its controller's untap step (the DOESNT_UNTAP static);
 *  (b) at an end step, if a creature died this turn, its Morbid trigger untaps it;
 *  (c) at an end step with no creature having died, the Morbid intervening-if fails and it stays tapped.
 */
class SlumberingCerberusScenarioTest : ScenarioTestBase() {

    private fun isTapped(game: TestGame, id: EntityId): Boolean =
        game.state.getEntity(id)?.has<TappedComponent>() == true

    init {
        context("Slumbering Cerberus") {

            test("does not untap during its controller's untap step") {
                // Cerberus (player 1) starts tapped. We are on player 2's turn, so passing priority
                // crosses into player 1's next turn — through player 1's untap step — before we stop.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Slumbering Cerberus", tapped = true)
                    .withCardOnBattlefield(1, "Mountain", tapped = true) // control: DOES untap
                    .withActivePlayer(2)
                    .inPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                    .build()

                val cerberus = game.findPermanent("Slumbering Cerberus")!!
                val mountain = game.findPermanent("Mountain")!!

                // Advance into player 1's turn, stopping at their upkeep (past the untap step).
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

                withClue("The control Mountain should have untapped during player 1's untap step") {
                    isTapped(game, mountain) shouldBe false
                }
                withClue("Slumbering Cerberus must NOT untap during its controller's untap step") {
                    isTapped(game, cerberus) shouldBe true
                }
            }

            test("Morbid untaps it at end step when a creature died this turn") {
                // Player 1's turn. Cerberus is tapped; a spare creature will die this turn.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Slumbering Cerberus", tapped = true)
                    .withCardOnBattlefield(1, "Savannah Lions") // 1/1 victim
                    .withCardOnBattlefield(1, "Mountain") // untapped, for the Bolt
                    .withCardInHand(1, "Lightning Bolt")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cerberus = game.findPermanent("Slumbering Cerberus")!!
                val lions = game.findPermanent("Savannah Lions")!!

                // Kill the Lions so "a creature died this turn" becomes true.
                val cast = game.castSpell(1, "Lightning Bolt", lions)
                withClue("Casting Lightning Bolt should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()
                withClue("Savannah Lions should have died to Lightning Bolt") {
                    game.isOnBattlefield("Savannah Lions") shouldBe false
                }
                withClue("Cerberus is still tapped before the end step") {
                    isTapped(game, cerberus) shouldBe true
                }

                // Advance to the end step; the Morbid trigger queues, then drain it.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Morbid: a creature died this turn, so Cerberus untaps at the end step") {
                    isTapped(game, cerberus) shouldBe false
                }
            }

            test("Morbid does not untap it at end step when no creature died") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Slumbering Cerberus", tapped = true)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cerberus = game.findPermanent("Slumbering Cerberus")!!

                // Advance straight to the end step; no creature has died this turn.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("No creature died this turn, so the Morbid intervening-if fails — Cerberus stays tapped") {
                    isTapped(game, cerberus) shouldBe true
                }
            }
        }
    }
}
