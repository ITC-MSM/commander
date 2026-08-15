package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Byrke, Long Ear of the Law (Bloomburrow #380) —
 * {4}{G}{W} Legendary Creature — Rabbit Soldier, 4/4.
 *
 *   Vigilance
 *   When Byrke enters, put a +1/+1 counter on each of up to two target creatures.
 *   Whenever a creature you control with a +1/+1 counter on it attacks, double the
 *   number of +1/+1 counters on it.
 *
 * Covers the ETB fan-out (one counter on each of two targets) and the attack-doubling
 * trigger (fires only for an attacker you control that already carries a +1/+1 counter,
 * and reads the post-declaration count, so the total is doubled).
 */
class ByrkeLongEarOfTheLawScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    private fun seedCounters(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, amount))
        }
    }

    init {
        context("Byrke, Long Ear of the Law") {

            test("ETB puts a +1/+1 counter on each of up to two target creatures") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withCardInHand(1, "Byrke, Long Ear of the Law")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!

                val cast = game.castSpell(1, "Byrke, Long Ear of the Law")
                withClue("Casting Byrke should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()
                // ETB trigger asks for up to two target creatures — choose both.
                game.selectTargets(listOf(bears, giant))
                game.resolveStack()

                withClue("Grizzly Bears gets one +1/+1 counter") {
                    plusOneCounters(game, bears) shouldBe 1
                }
                withClue("Hill Giant gets one +1/+1 counter") {
                    plusOneCounters(game, giant) shouldBe 1
                }
            }

            test("attacking with a counter-bearing creature you control doubles its +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Byrke, Long Ear of the Law", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                seedCounters(game, bears, 3)

                game.declareAttackers(mapOf("Grizzly Bears" to 2))
                // The doubling trigger goes on the stack; resolve it.
                game.resolveStack()

                withClue("Grizzly Bears' 3 +1/+1 counters are doubled to 6") {
                    plusOneCounters(game, bears) shouldBe 6
                }
            }

            test("attacking with a creature that has no +1/+1 counter does not trigger doubling") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Byrke, Long Ear of the Law", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                // Attack with both Byrke (no counter) and a counterless Bears — neither qualifies.
                game.declareAttackers(mapOf("Grizzly Bears" to 2, "Byrke, Long Ear of the Law" to 2))
                game.resolveStack()

                withClue("A counterless attacker is untouched — no counters appear") {
                    plusOneCounters(game, bears) shouldBe 0
                }
            }
        }
    }
}
