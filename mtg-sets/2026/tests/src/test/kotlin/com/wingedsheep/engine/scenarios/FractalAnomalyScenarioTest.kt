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
 * Fractal Anomaly {U} Instant — "Create a 0/0 green and blue Fractal creature token and put X
 * +1/+1 counters on it, where X is the number of cards you've drawn this turn."
 *
 * Verifies the Fractal token enters with one +1/+1 counter per card drawn this turn (here X is
 * sourced from `TurnTracking(You, CARDS_DRAWN)`), and that with no cards drawn it enters as a 0/0
 * with no counters.
 */
class FractalAnomalyScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Fractal Anomaly — Fractal token with X = cards drawn this turn") {

            test("after drawing two cards, the Fractal enters with two +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Divination")     // {2}{U} sorcery — draw two
                    .withCardInHand(1, "Fractal Anomaly") // {U} instant
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(1, "Forest")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Draw two via Divination so CARDS_DRAWN this turn = 2.
                game.castSpell(1, "Divination").error shouldBe null
                game.resolveStack()

                game.castSpell(1, "Fractal Anomaly").error shouldBe null
                game.resolveStack()

                val fractal = game.findPermanent("Fractal Token")
                withClue("A Fractal token should have been created") {
                    (fractal != null) shouldBe true
                }
                withClue("Two cards drawn this turn → two +1/+1 counters on the 0/0 Fractal") {
                    plusOneCounters(game, fractal!!) shouldBe 2
                }
            }

            test("with no cards drawn this turn, the Fractal is a 0/0 and dies to state-based actions") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Fractal Anomaly")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Fractal Anomaly").error shouldBe null
                game.resolveStack()

                // X = 0 → the token enters as a 0/0 with no counters and is put into the
                // graveyard immediately by state-based actions (CR 704.5f). A token ceases to
                // exist once it leaves the battlefield, so none remains.
                withClue("0/0 Fractal token with no counters dies to SBAs") {
                    (game.findPermanent("Fractal Token") == null) shouldBe true
                }
            }
        }
    }
}
