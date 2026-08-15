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
 * Scenario tests for Sovereign Okinec Ahau (LCI #240) — {2}{G}{W} Legendary Creature — Cat Noble (3/4).
 *
 * "Whenever Sovereign Okinec Ahau attacks, for each creature you control with power greater than that
 * creature's base power, put a number of +1/+1 counters on that creature equal to the difference."
 *
 * The payoff composes a SELF attack trigger with a `ForEachInGroup` over
 * `Creature.youControl().powerGreaterThanBase()`, adding `Subtract(EntityProperty(IterationEntity, Power),
 * EntityProperty(IterationEntity, BasePower))` +1/+1 counters to each iterated creature — the new
 * `EntityNumericProperty.BasePower` read, kept consistent with the `PowerGreaterThanBase` filter so the
 * "difference" is always ≥ 1 for a group member. These tests exercise: a counter-pumped creature, an
 * anthem-pumped creature (including the Sovereign itself), the no-qualifier no-op, and distinct
 * per-creature differences.
 */
class SovereignOkinecAhauScenarioTest : ScenarioTestBase() {

    private fun addPlusOneCounters(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) {
            it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to amount)))
        }
    }

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Sovereign Okinec Ahau's attack trigger — counters equal to (power − base power)") {

            test("a creature pumped above base by a +1/+1 counter gets counters equal to the difference") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Sovereign Okinec Ahau", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                // Bear is 2/2; a single +1/+1 counter -> projected power 3, base power 2, difference 1.
                val bear = game.findPermanent("Grizzly Bears")!!
                val sovereign = game.findPermanent("Sovereign Okinec Ahau")!!
                addPlusOneCounters(game, bear, 1)

                game.declareAttackers(mapOf("Sovereign Okinec Ahau" to 2)).error shouldBe null
                game.resolveStack()

                withClue("Bear received exactly the difference (1) more, for 2 total") {
                    plusOneCounters(game, bear) shouldBe 2
                }
                withClue("The unmodified Sovereign (power 3 == base 3) is not a qualifying creature") {
                    plusOneCounters(game, sovereign) shouldBe 0
                }
            }

            test("an anthem pumps the Sovereign itself and another creature above base — each gets its difference") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Sovereign Okinec Ahau", summoningSickness = false)
                    .withCardOnBattlefield(1, "Glorious Anthem")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                // Glorious Anthem: creatures you control get +1/+1. Sovereign -> 4/5 (base 3, diff 1);
                // Bear -> 3/3 (base 2, diff 1).
                val bear = game.findPermanent("Grizzly Bears")!!
                val sovereign = game.findPermanent("Sovereign Okinec Ahau")!!

                game.declareAttackers(mapOf("Sovereign Okinec Ahau" to 2)).error shouldBe null
                game.resolveStack()

                withClue("The Sovereign is pumped above its own base by the anthem, so it qualifies for 1 counter") {
                    plusOneCounters(game, sovereign) shouldBe 1
                }
                withClue("The Bear is pumped above its base by the anthem, so it gets 1 counter") {
                    plusOneCounters(game, bear) shouldBe 1
                }
            }

            test("no creature above its base power — the trigger puts no counters") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Sovereign Okinec Ahau", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!
                val sovereign = game.findPermanent("Sovereign Okinec Ahau")!!

                game.declareAttackers(mapOf("Sovereign Okinec Ahau" to 2)).error shouldBe null
                game.resolveStack()

                withClue("An unmodified 2/2 Bear has power == base, so it is not a qualifying creature") {
                    plusOneCounters(game, bear) shouldBe 0
                }
                withClue("The unmodified Sovereign is likewise excluded") {
                    plusOneCounters(game, sovereign) shouldBe 0
                }
            }

            test("two creatures with different excesses each get their own difference") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Sovereign Okinec Ahau", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!      // 2/2
                val giant = game.findPermanent("Hill Giant")!!        // 3/3
                addPlusOneCounters(game, bear, 2)   // power 4, base 2 -> difference 2
                addPlusOneCounters(game, giant, 1)  // power 4, base 3 -> difference 1

                game.declareAttackers(mapOf("Sovereign Okinec Ahau" to 2)).error shouldBe null
                game.resolveStack()

                withClue("Bear gains its own difference (2), for 4 total") {
                    plusOneCounters(game, bear) shouldBe 4
                }
                withClue("Hill Giant gains its own difference (1), for 2 total") {
                    plusOneCounters(game, giant) shouldBe 2
                }
            }
        }
    }
}
