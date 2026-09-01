package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Sower of Temptation (LRW #88) — "Flying. When this creature enters, gain control of target
 * creature for as long as this creature remains on the battlefield."
 *
 * The interesting half is the duration, not the grab. `Duration.WhileSourceOnBattlefield` has to
 * survive an ordinary turn boundary (an "until end of turn" steal would not) and has to hand the
 * creature back the moment Sower leaves — which is the whole reason the card is answerable by
 * killing the Faerie rather than by racing a clock.
 */
class SowerOfTemptationScenarioTest : ScenarioTestBase() {

    init {
        context("Sower of Temptation") {

            fun TestGame.resolveGrab(victim: com.wingedsheep.sdk.model.EntityId) {
                resolveStack()
                state.pendingDecision?.let {
                    submitDecision(TargetsResponse(it.id, mapOf(0 to listOf(victim))))
                }
                resolveStack()
            }

            test("steals a creature on entry and keeps it across the turn boundary") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sower of Temptation")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Sower of Temptation").error shouldBe null
                game.resolveGrab(bears)

                withClue("The Bears changed hands as the Faerie entered") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }

                game.passUntilPhase(Phase.ENDING, Step.CLEANUP)

                withClue("The steal is not \"until end of turn\" — it survives cleanup") {
                    game.state.projectedState.getController(bears) shouldBe game.player1Id
                }
            }

            test("the creature goes home the moment Sower leaves the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sower of Temptation")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Sower of Temptation").error shouldBe null
                game.resolveGrab(bears)
                game.state.projectedState.getController(bears) shouldBe game.player1Id

                // Shock the 2/2 Faerie down. Its own controller can do this — the duration is keyed
                // to the permanent being on the battlefield, not to who owns it.
                val sower = game.findPermanent("Sower of Temptation")!!
                game.castSpell(1, "Shock", sower).error shouldBe null
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("Sower died, so the \"for as long as\" duration ended") {
                    game.findPermanent("Sower of Temptation") shouldBe null
                    game.state.projectedState.getController(bears) shouldBe game.player2Id
                }
            }
        }
    }
}
