package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tla.cards.MaiJadedEdge
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Mai, Jaded Edge (TLA #147).
 *
 * {1}{R} 1/3 Human Noble, Prowess.
 * Exhaust — {3}: Put a double strike counter on Mai.
 */
class MaiJadedEdgeScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(MaiJadedEdge)

        context("Mai, Jaded Edge") {

            test("exhaust puts a double strike counter, granting Double Strike") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Mai, Jaded Edge", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mai = game.findPermanent("Mai, Jaded Edge")!!
                val abilityId = cardRegistry.getCard("Mai, Jaded Edge")!!.script.activatedAbilities[0].id

                withClue("no double strike before activation") {
                    game.state.projectedState.hasKeyword(mai, Keyword.DOUBLE_STRIKE) shouldBe false
                }

                game.execute(ActivateAbility(game.player1Id, mai, abilityId)).error shouldBe null
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                game.state.getEntity(mai)?.get<CountersComponent>()
                    ?.getCount(CounterType.DOUBLE_STRIKE) shouldBe 1
                withClue("the double strike counter projects the Double Strike keyword") {
                    game.state.projectedState.hasKeyword(mai, Keyword.DOUBLE_STRIKE) shouldBe true
                }
            }
        }
    }
}
