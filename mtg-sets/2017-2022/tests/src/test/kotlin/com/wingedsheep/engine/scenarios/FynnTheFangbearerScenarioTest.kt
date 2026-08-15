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
 * Fynn, the Fangbearer (KHM #170, reprinted as FDN #637) — {1}{G} 1/3 Legendary Creature.
 *
 * "Deathtouch
 *  Whenever a creature you control with deathtouch deals combat damage to a player, that
 *  player gets two poison counters."
 *
 * Pins the trigger's source filter: it fires for Fynn itself (which has printed
 * deathtouch), for another deathtouch creature you control, and *not* for a creature
 * without deathtouch.
 */
class FynnTheFangbearerScenarioTest : ScenarioTestBase() {

    private fun poisonCounters(playerNumber: Int, game: TestGame): Int {
        val playerId = EntityId.of("player-$playerNumber")
        return game.state.getEntity(playerId)?.get<CountersComponent>()?.getCount(CounterType.POISON) ?: 0
    }

    init {
        context("Fynn, the Fangbearer") {

            test("Fynn's own combat damage gives the defending player two poison counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Fynn, the Fangbearer")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                poisonCounters(2, game) shouldBe 0

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Fynn, the Fangbearer" to 2)).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Fynn has deathtouch, so his own combat damage triggers his ability") {
                    poisonCounters(2, game) shouldBe 2
                }
                game.getLifeTotal(2) shouldBe 19
            }

            test("another deathtouch creature you control also triggers it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Fynn, the Fangbearer")
                    .withCardOnBattlefield(1, "Typhoid Rats") // 1/1 deathtouch
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Typhoid Rats" to 2)).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("the Rats have deathtouch and are controlled by Fynn's controller") {
                    poisonCounters(2, game) shouldBe 2
                }
            }

            test("a creature without deathtouch does not trigger it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Fynn, the Fangbearer")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("the Bears have no deathtouch, so no poison is dealt") {
                    poisonCounters(2, game) shouldBe 0
                }
                game.getLifeTotal(2) shouldBe 18
            }
        }
    }
}
