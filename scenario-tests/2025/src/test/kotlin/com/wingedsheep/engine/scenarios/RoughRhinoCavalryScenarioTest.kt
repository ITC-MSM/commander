package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tla.cards.RoughRhinoCavalry
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Rough Rhino Cavalry (TLA #152).
 *
 * {4}{R} 5/5 Human Mercenary, Firebending 2.
 * Exhaust — {8}: Put two +1/+1 counters on this creature. It gains trample until end of turn.
 */
class RoughRhinoCavalryScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(RoughRhinoCavalry)

        context("Rough Rhino Cavalry") {

            test("exhaust adds two +1/+1 counters and grants trample until end of turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Rough Rhino Cavalry", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 8)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val rhino = game.findPermanent("Rough Rhino Cavalry")!!
                val abilityId = cardRegistry.getCard("Rough Rhino Cavalry")!!.script.activatedAbilities[0].id

                withClue("trample is not granted before activation") {
                    game.state.projectedState.hasKeyword(rhino, Keyword.TRAMPLE) shouldBe false
                }

                game.execute(ActivateAbility(game.player1Id, rhino, abilityId)).error shouldBe null
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                game.state.getEntity(rhino)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                withClue("the exhaust ability grants trample until end of turn") {
                    game.state.projectedState.hasKeyword(rhino, Keyword.TRAMPLE) shouldBe true
                }
            }
        }
    }
}
