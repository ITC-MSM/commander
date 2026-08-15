package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Cat Collector (Foundations).
 *
 * {2}{W} Creature — Human Citizen 3/2
 * - When this creature enters, create a Food token.
 * - Whenever you gain life for the first time during each of your turns, create a
 *   1/1 white Cat creature token.
 *
 * Exercises the [com.wingedsheep.sdk.dsl.Effects.CreateFood] ETB and the
 * `YouGainLifeFirstTimeEachTurn` trigger — the latter fires only on the first life gain per turn.
 */
class CatCollectorScenarioTest : ScenarioTestBase() {

    init {
        context("Cat Collector") {

            test("ETB creates a Food token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Cat Collector")
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Cat Collector")
                game.resolveStack()

                withClue("Cat Collector should be on the battlefield") {
                    game.isOnBattlefield("Cat Collector") shouldBe true
                }
                withClue("ETB creates exactly one Food token") {
                    game.findPermanents("Food").size shouldBe 1
                }
                withClue("No Cat token exists before any life is gained") {
                    game.findPermanents("Cat Token").size shouldBe 0
                }
            }

            test("first life gain each turn creates a Cat token; a second gain the same turn does not") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Cat Collector", summoningSickness = false)
                    .withCardInHand(1, "Venerable Monk")
                    .withCardInHand(1, "Venerable Monk")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Venerable Monk's ETB gains 2 life — the first life gain this turn.
                game.castSpell(1, "Venerable Monk")
                game.resolveStack()

                withClue("First life gain this turn creates one 1/1 white Cat token") {
                    game.findPermanents("Cat Token").size shouldBe 1
                }

                // A second life gain the same turn no longer satisfies "for the first time each turn".
                game.castSpell(1, "Venerable Monk")
                game.resolveStack()

                withClue("A second same-turn life gain must not create another Cat token") {
                    game.findPermanents("Cat Token").size shouldBe 1
                }
            }
        }
    }
}
