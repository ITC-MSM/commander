package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Impractical Joke {R} Sorcery (Secrets of Strixhaven #119).
 *
 * "Damage can't be prevented this turn.
 *  Impractical Joke deals 3 damage to up to one target creature or planeswalker."
 *
 * Covers the 3-damage burn against an "up to one" optional target (a 2/2 dies), and the
 * optional-target path: cast with no target still resolves (lifting damage prevention for the
 * turn).
 */
class ImpracticalJokeScenarioTest : ScenarioTestBase() {

    init {
        context("Impractical Joke — burn with optional target") {

            test("deals 3 damage to a target creature, killing a 2/2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Impractical Joke")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Impractical Joke", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("3 damage destroys the 2/2 Grizzly Bears") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }

            test("can be cast with no target; it still resolves") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Impractical Joke")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Impractical Joke").error shouldBe null
                game.resolveStack()

                withClue("Impractical Joke resolves to the graveyard with no target chosen") {
                    game.isInGraveyard(1, "Impractical Joke") shouldBe true
                }
            }
        }
    }
}
