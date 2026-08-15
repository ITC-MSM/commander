package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Torpor Orb (New Phyrexia):
 * "Creatures entering don't cause abilities to trigger."
 *
 * Pins the documented rulings:
 *  - A creature's own enters-the-battlefield triggered ability is suppressed (Venerable Monk's
 *    "gain 2 life" does not fire).
 *  - Without Torpor Orb the same ETB trigger fires normally (control).
 *  - Replacement effects are unaffected — a creature that enters with a +1/+1 counter
 *    (District Mascot) still gets the counter.
 */
class TorporOrbScenarioTest : ScenarioTestBase() {

    init {
        context("Torpor Orb suppresses enters-the-battlefield triggers of creatures") {
            test("Venerable Monk's ETB life gain does not fire while Torpor Orb is in play") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Torpor Orb")
                    .withCardInHand(1, "Venerable Monk")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Venerable Monk")
                withClue("Venerable Monk should be castable") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Venerable Monk should be on the battlefield") {
                    (game.findPermanent("Venerable Monk") != null) shouldBe true
                }
                withClue("ETB life-gain must be suppressed by Torpor Orb") {
                    game.getLifeTotal(1) shouldBe 20
                }
            }

            test("Without Torpor Orb, Venerable Monk's ETB life gain fires normally") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Venerable Monk")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Venerable Monk")
                withClue("Venerable Monk should be castable") { cast.error shouldBe null }
                game.resolveStack()

                withClue("ETB life-gain should fire when no Torpor Orb is present") {
                    game.getLifeTotal(1) shouldBe 22
                }
            }
        }

        context("Torpor Orb leaves replacement effects untouched") {
            test("District Mascot still enters with its +1/+1 counter") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Torpor Orb")
                    .withCardInHand(1, "District Mascot")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "District Mascot")
                withClue("District Mascot should be castable") { cast.error shouldBe null }
                game.resolveStack()

                val mascot = game.findPermanent("District Mascot")!!
                val counters = game.state.getEntity(mascot)?.get<CountersComponent>()
                withClue("Enters-with-counter is a replacement effect, unaffected by Torpor Orb") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
                }
            }
        }
    }
}
