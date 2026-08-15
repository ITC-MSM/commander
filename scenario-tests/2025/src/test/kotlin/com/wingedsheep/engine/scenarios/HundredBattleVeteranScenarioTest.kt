package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Hundred-Battle Veteran ({3}{B}, 4/2 Zombie Warrior).
 *
 * Exercises the new "three or more different kinds of counters among creatures you control"
 * static condition (DISTINCT_COUNTER_TYPES aggregation) gating a +2/+4 self-buff, plus the
 * "cast from graveyard → enters with a finality counter" recursion.
 */
class HundredBattleVeteranScenarioTest : ScenarioTestBase() {

    init {
        context("Hundred-Battle Veteran — counter-kinds buff") {

            test("gets +2/+4 once three different kinds of counters are among creatures you control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hundred-Battle Veteran")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val veteran = game.findPermanent("Hundred-Battle Veteran")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!

                // Two distinct kinds of counters → buff is off, base 4/2.
                game.state = game.state.updateEntity(bears) {
                    it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
                }
                game.state = game.state.updateEntity(giant) {
                    it.with(CountersComponent(mapOf(CounterType.FLYING to 1)))
                }
                withClue("Two kinds of counters → no buff, base 4/2") {
                    game.state.projectedState.getPower(veteran) shouldBe 4
                    game.state.projectedState.getToughness(veteran) shouldBe 2
                }

                // A third distinct kind crosses the threshold → +2/+4.
                game.state = game.state.updateEntity(veteran) {
                    it.with(CountersComponent(mapOf(CounterType.FIRST_STRIKE to 1)))
                }
                withClue("Three kinds of counters → +2/+4, now 6/6") {
                    game.state.projectedState.getPower(veteran) shouldBe 6
                    game.state.projectedState.getToughness(veteran) shouldBe 6
                }
            }

            test("the same kind on several creatures counts only once") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hundred-Battle Veteran")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val veteran = game.findPermanent("Hundred-Battle Veteran")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!

                // Two creatures share +1/+1 (one kind) and a third has flying (second kind).
                game.state = game.state.updateEntity(bears) {
                    it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
                }
                game.state = game.state.updateEntity(giant) {
                    it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
                }
                game.state = game.state.updateEntity(veteran) {
                    it.with(CountersComponent(mapOf(CounterType.FLYING to 1)))
                }
                withClue("Duplicate +1/+1 counts once → only two kinds → no buff") {
                    game.state.projectedState.getPower(veteran) shouldBe 4
                    game.state.projectedState.getToughness(veteran) shouldBe 2
                }
            }

            test("counters on the Veteran itself count toward the total") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hundred-Battle Veteran")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val veteran = game.findPermanent("Hundred-Battle Veteran")!!

                // All three kinds on the Veteran — it is itself a creature you control.
                game.state = game.state.updateEntity(veteran) {
                    it.with(
                        CountersComponent(
                            mapOf(
                                CounterType.PLUS_ONE_PLUS_ONE to 1,
                                CounterType.FLYING to 1,
                                CounterType.FIRST_STRIKE to 1
                            )
                        )
                    )
                }
                withClue("Three kinds on itself → +2/+4. Base 4/2 + (+1/+1 counter) + (+2/+4) = 7/7") {
                    // 4/2 base, +1/+1 from the +1/+1 counter (layer 7c), +2/+4 from the static buff.
                    game.state.projectedState.getPower(veteran) shouldBe 7
                    game.state.projectedState.getToughness(veteran) shouldBe 7
                }
            }

            test("counters on creatures you don't control are ignored") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hundred-Battle Veteran")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val veteran = game.findPermanent("Hundred-Battle Veteran")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val giant = game.findPermanent("Hill Giant")!!

                // Three distinct kinds, but spread across the opponent's creatures.
                game.state = game.state.updateEntity(bears) {
                    it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1, CounterType.FLYING to 1)))
                }
                game.state = game.state.updateEntity(giant) {
                    it.with(CountersComponent(mapOf(CounterType.FIRST_STRIKE to 1)))
                }
                withClue("Opponent's counters don't count → no buff, base 4/2") {
                    game.state.projectedState.getPower(veteran) shouldBe 4
                    game.state.projectedState.getToughness(veteran) shouldBe 2
                }
            }
        }

        context("Hundred-Battle Veteran — graveyard recursion") {

            test("cast from the graveyard, it enters with a finality counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInGraveyard(1, "Hundred-Battle Veteran")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpellFromGraveyard(1, "Hundred-Battle Veteran")
                withClue("Casting Hundred-Battle Veteran from the graveyard should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val veteran = game.findPermanent("Hundred-Battle Veteran")!!
                val counters = game.state.getEntity(veteran)?.get<CountersComponent>()
                withClue("Cast from graveyard → enters with one finality counter") {
                    (counters?.getCount(CounterType.FINALITY) ?: 0) shouldBe 1
                }
            }

            test("cast from hand, it does NOT enter with a finality counter") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Hundred-Battle Veteran")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Hundred-Battle Veteran")
                withClue("Casting Hundred-Battle Veteran from hand should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val veteran = game.findPermanent("Hundred-Battle Veteran")!!
                val counters = game.state.getEntity(veteran)?.get<CountersComponent>()
                withClue("Cast from hand → no finality counter") {
                    (counters?.getCount(CounterType.FINALITY) ?: 0) shouldBe 0
                }
            }
        }
    }
}
