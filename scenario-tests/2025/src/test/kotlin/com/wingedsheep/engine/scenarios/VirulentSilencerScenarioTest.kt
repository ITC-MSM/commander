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
 * Scenario test for Virulent Silencer (EOE #248) — {3} Artifact Creature, Uncommon.
 *
 * "Whenever a nontoken artifact creature you control deals combat damage to a player,
 *  that player gets two poison counters."
 *
 * Regression for issue #1300: the trigger fired but placed no poison counters, both when
 * Virulent Silencer itself dealt the damage and when another nontoken artifact creature did.
 * "That player" must resolve to the *damaged* player (the trigger's recipient).
 */
class VirulentSilencerScenarioTest : ScenarioTestBase() {

    private fun poisonCounters(playerNumber: Int, game: TestGame): Int {
        val playerId = EntityId.of("player-$playerNumber")
        return game.state.getEntity(playerId)?.get<CountersComponent>()?.getCount(CounterType.POISON) ?: 0
    }

    init {
        context("Virulent Silencer") {

            test("gives the damaged player two poison counters when it deals combat damage itself") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Virulent Silencer")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("no poison counters before combat") {
                    poisonCounters(2, game) shouldBe 0
                }

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Virulent Silencer" to 2)).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Virulent Silencer's combat damage gives the opponent two poison counters") {
                    poisonCounters(2, game) shouldBe 2
                }
                withClue("the opponent took 2 combat damage") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }

            test("gives the damaged player two poison counters when another artifact creature deals the damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Virulent Silencer")
                    .withCardOnBattlefield(1, "Alpha Myr") // 2/1 nontoken artifact creature
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Alpha Myr" to 2)).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Alpha Myr (a nontoken artifact creature you control) triggers Virulent Silencer") {
                    poisonCounters(2, game) shouldBe 2
                }
            }
        }
    }
}
