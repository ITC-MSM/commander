package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Scolding Administrator (Secrets of Strixhaven #224).
 *
 * Scolding Administrator ({W}{B}, 2/2, Dwarf Cleric):
 *   Menace
 *   Repartee — Whenever you cast an instant or sorcery spell that targets a creature, put a +1/+1
 *     counter on this creature.
 *   When this creature dies, if it had counters on it, put those counters on up to one target
 *     creature.
 *
 * Exercises the Repartee counter trigger and the conditional dies trigger that moves all counters
 * to up to one target creature.
 */
class ScoldingAdministratorScenarioTest : ScenarioTestBase() {

    private fun TestGame.plusCounters(name: String): Int {
        val id = findPermanent(name) ?: return -1
        return state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
    }

    init {
        context("Scolding Administrator — Repartee counter + counter-moving dies trigger") {

            test("casting an instant targeting a creature puts a +1/+1 counter on it") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Scolding Administrator", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Giant Growth")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(6) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(6) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                withClue("no counters before casting") {
                    game.plusCounters("Scolding Administrator") shouldBe 0
                }

                // Cast Giant Growth (an instant) targeting Grizzly Bears (a creature) — triggers Repartee.
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Giant Growth", bears)
                game.resolveStack()

                withClue("Repartee puts a +1/+1 counter on Scolding Administrator") {
                    game.plusCounters("Scolding Administrator") shouldBe 1
                }
            }

            test("dying with counters moves them to up to one target creature") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Scolding Administrator", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(6) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(6) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val scolding = game.findPermanent("Scolding Administrator")!!

                // Lightning Bolt (an instant) targets Scolding (a creature), so it first triggers
                // Repartee. That trigger resolves before the bolt (LIFO), putting a +1/+1 counter on
                // Scolding (now 3/3); then the bolt deals 3 damage and Scolding dies holding 1 counter.
                game.castSpell(1, "Lightning Bolt", scolding)
                game.resolveStack()

                withClue("Scolding Administrator has died") {
                    game.findPermanent("Scolding Administrator") shouldBe null
                }

                // The dies trigger asks for up to one target creature; choose Grizzly Bears.
                withClue("a target decision should be pending for the counter-move") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("the +1/+1 counter moved to Grizzly Bears") {
                    game.plusCounters("Grizzly Bears") shouldBe 1
                }
            }

            test("dying with no counters does nothing (intervening-if fails)") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Scolding Administrator", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Infest")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(6) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(6) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                // Infest ("All creatures get -2/-2") does NOT target a creature, so Repartee never
                // fires — Scolding dies as a 0/0 with zero counters, and the dies trigger's
                // intervening "if it had counters on it" is not met.
                game.castSpell(1, "Infest")
                game.resolveStack()

                withClue("Scolding Administrator has died") {
                    game.findPermanent("Scolding Administrator") shouldBe null
                }
                withClue("with no counters the dies trigger does not fire, so no target decision") {
                    game.hasPendingDecision() shouldBe false
                }
            }
        }
    }
}
