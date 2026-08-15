package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Perennation (TDM #212) — {3}{W}{B}{G} Sorcery.
 *
 * "Return target permanent card from your graveyard to the battlefield with a hexproof counter
 *  and an indestructible counter on it."
 *
 * Confirms the reanimation puts the targeted creature card back onto the battlefield with one
 * hexproof and one indestructible counter, and that those keyword counters grant the keywords
 * through projected state.
 */
class PerennationScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Perennation reanimation with keyword counters") {

            test("returns a creature card from graveyard with hexproof + indestructible counters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Perennation")
                    .withCardInGraveyard(1, "Hill Giant") // 3/3 permanent card
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpellTargetingGraveyardCard(1, "Perennation", 1, "Hill Giant")
                withClue("Casting Perennation should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Hill Giant is returned to the battlefield") {
                    game.isOnBattlefield("Hill Giant") shouldBe true
                }
                val giant = game.findPermanent("Hill Giant")!!

                val counters = game.state.getEntity(giant)?.get<CountersComponent>()?.counters
                withClue("It has a hexproof counter") {
                    counters?.get(CounterType.HEXPROOF) shouldBe 1
                }
                withClue("It has an indestructible counter") {
                    counters?.get(CounterType.INDESTRUCTIBLE) shouldBe 1
                }

                val projected = stateProjector.project(game.state)
                withClue("The hexproof counter grants hexproof via projection") {
                    projected.hasKeyword(giant, Keyword.HEXPROOF) shouldBe true
                }
                withClue("The indestructible counter grants indestructible via projection") {
                    projected.hasKeyword(giant, Keyword.INDESTRUCTIBLE) shouldBe true
                }
            }
        }
    }
}
