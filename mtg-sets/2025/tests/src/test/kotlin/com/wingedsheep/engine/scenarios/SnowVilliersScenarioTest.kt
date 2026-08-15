package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Snow Villiers (FIN) — {2}{W} Legendary Creature — Human Rebel Monk * /3.
 *
 *  "Vigilance
 *   Snow Villiers's power is equal to the number of creatures you control."
 *
 * Verifies the characteristic-defining power (recomputes with the number of creatures you control,
 * counting Snow himself), the fixed toughness of 3, and vigilance.
 */
class SnowVilliersScenarioTest : ScenarioTestBase() {

    init {
        context("Snow Villiers CDA power") {

            test("power equals the number of creatures you control (Snow counts himself)") {
                val alone = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Snow Villiers", summoningSickness = false)
                    .build()

                val snowAlone = alone.findPermanent("Snow Villiers")!!
                withClue("Alone, Snow is the only creature you control → power 1, toughness 3") {
                    alone.state.projectedState.getPower(snowAlone) shouldBe 1
                    alone.state.projectedState.getToughness(snowAlone) shouldBe 3
                }

                val withFriends = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Snow Villiers", summoningSickness = false)
                    .withCardOnBattlefield(1, "Savannah Lions", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    // An opponent's creature must NOT count.
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .build()

                val snow = withFriends.findPermanent("Snow Villiers")!!
                withClue("Three creatures you control (Snow + two) → power 3; opponent's creature ignored") {
                    withFriends.state.projectedState.getPower(snow) shouldBe 3
                    withFriends.state.projectedState.getToughness(snow) shouldBe 3
                }
            }

            test("has vigilance") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Snow Villiers", summoningSickness = false)
                    .build()

                val snow = game.findPermanent("Snow Villiers")!!
                game.state.projectedState.hasKeyword(snow, Keyword.VIGILANCE) shouldBe true
            }
        }
    }
}
