package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Noctis, Prince of Lucis (FIN) — the artifact analogue of Leonardo, Sewer Samurai.
 *
 * Casting an artifact from your graveyard via Noctis's permission costs 3 life in addition to its
 * other costs, and the artifact enters with a finality counter. Exercises [MayCastFromGraveyard]
 * (filtered to artifacts, lifeCost = 3) plus the non-self `EntersWithCounters(FINALITY,
 * condition = WasCastFromGraveyard)` whose `appliesTo` is overridden to artifacts-you-control.
 */
class NoctisPrinceOfLucisScenarioTest : ScenarioTestBase() {

    init {
        context("Noctis, Prince of Lucis") {

            test("an artifact cast from the graveyard via Noctis costs 3 life and enters with a finality counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Noctis, Prince of Lucis")
                    .withCardInGraveyard(1, "Ornithopter") // {0} Artifact Creature — no mana needed
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Cast Ornithopter ({0}) from the graveyard via Noctis, paying the 3-life additional cost.
                val ornithopterInGrave = game.findCardsInGraveyard(1, "Ornithopter").first()
                val cast = game.execute(
                    CastSpell(
                        playerId = game.state.activePlayerId!!,
                        cardId = ornithopterInGrave,
                        graveyardLifeCost = 3
                    )
                )
                withClue("Casting an artifact from the graveyard via Noctis should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("The 3-life additional cost should have been paid (20 -> 17)") {
                    game.getLifeTotal(1) shouldBe 17
                }

                val ornithopter = game.findPermanent("Ornithopter")!!
                val counters = game.state.getEntity(ornithopter)?.get<CountersComponent>()
                withClue("Cast from graveyard via Noctis → enters with one finality counter") {
                    (counters?.getCount(CounterType.FINALITY) ?: 0) shouldBe 1
                }
            }
        }
    }
}
