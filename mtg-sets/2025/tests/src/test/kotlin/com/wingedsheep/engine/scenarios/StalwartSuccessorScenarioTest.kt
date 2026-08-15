package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import com.wingedsheep.sdk.model.EntityId

/**
 * Scenario tests for Stalwart Successor (Tarkir: Dragonstorm #227).
 *
 * Stalwart Successor ({1}{B}{G}, 3/2, Menace):
 *   "Whenever one or more counters are put on a creature you control, if it's the first time
 *    counters have been put on that creature this turn, put a +1/+1 counter on that creature."
 *
 * Exercises the engine's new `Triggers.countersPlacedOn` (any counter type, first-time-per-turn
 * gate via `ReceivedCountersThisTurnComponent`). Sage of the Fang's ETB ("put a +1/+1 counter on
 * target creature") is the counter source.
 */
class StalwartSuccessorScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Stalwart Successor — first counters on a creature you control") {

            test("first +1/+1 counter this turn triggers Stalwart for an extra counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stalwart Successor")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Sage of the Fang")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = game.castSpell(1, "Sage of the Fang")
                withClue("Casting Sage of the Fang should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // Sage's ETB asks for a target — choose our Grizzly Bears.
                withClue("Sage ETB should prompt for a counter target") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(bears))
                game.resolveStack()

                // Sage places the first +1/+1 (first counters this turn) → Stalwart adds one more.
                withClue("Grizzly Bears should have 2 +1/+1 counters (Sage's + Stalwart's)") {
                    plusOneCounters(game, bears) shouldBe 2
                }
            }

            test("a second counter the same turn does not re-trigger Stalwart") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stalwart Successor")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Sage of the Fang")
                    .withCardInHand(1, "Sage of the Fang")
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                // First Sage: 1 (Sage) + 1 (Stalwart) = 2 counters.
                game.castSpell(1, "Sage of the Fang")
                game.resolveStack()
                game.selectTargets(listOf(bears))
                game.resolveStack()
                withClue("After first Sage, Grizzly Bears has 2 counters") {
                    plusOneCounters(game, bears) shouldBe 2
                }

                // Second Sage same turn: Sage adds 1 (now 3), but counters were already put on
                // Grizzly Bears this turn, so Stalwart does NOT trigger again.
                game.castSpell(1, "Sage of the Fang")
                game.resolveStack()
                game.selectTargets(listOf(bears))
                game.resolveStack()
                withClue("Second counter source must not re-trigger Stalwart (3, not 4)") {
                    plusOneCounters(game, bears) shouldBe 3
                }
            }

            test("a creature you control entering with counters triggers Stalwart") {
                // CR: a permanent entering with counters has those counters "put on" it (the
                // controller is the one putting them), so Stalwart's "first time counters this
                // turn" trigger fires. Dockworker Drone enters with one +1/+1 counter; Stalwart
                // adds a second.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stalwart Successor")
                    .withCardInHand(1, "Dockworker Drone")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Dockworker Drone")
                withClue("Casting Dockworker Drone should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val drone = game.findPermanent("Dockworker Drone")!!
                withClue("Dockworker Drone should have 2 +1/+1 counters (enters-with + Stalwart)") {
                    plusOneCounters(game, drone) shouldBe 2
                }
            }

            test("counters on a creature an opponent controls do not trigger Stalwart") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Stalwart Successor")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Sage of the Fang")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentBears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Sage of the Fang")
                game.resolveStack()
                game.selectTargets(listOf(opponentBears))
                game.resolveStack()

                withClue("Opponent's Grizzly Bears should have only Sage's 1 counter") {
                    plusOneCounters(game, opponentBears) shouldBe 1
                }
            }
        }
    }
}
