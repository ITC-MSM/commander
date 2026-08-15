package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tla.cards.HogMonkey
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Hog-Monkey (TLA #104).
 *
 * {2}{B} 3/2 Boar Monkey.
 * At the beginning of combat on your turn, target creature you control with a +1/+1 counter on it
 * gains menace until end of turn.
 * Exhaust — {5}: Put two +1/+1 counters on this creature.
 */
class HogMonkeyScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(HogMonkey)

        context("Hog-Monkey") {

            test("exhaust puts two +1/+1 counters; the begin-combat trigger then grants it menace") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Hog-Monkey", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val hog = game.findPermanent("Hog-Monkey")!!
                val abilityId = cardRegistry.getCard("Hog-Monkey")!!.script.activatedAbilities[0].id

                // Exhaust — {5}: two +1/+1 counters.
                game.execute(ActivateAbility(game.player1Id, hog, abilityId)).error shouldBe null
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()
                game.state.getEntity(hog)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2

                // Advance into combat: the begin-combat trigger targets the (now counter-bearing) Hog-Monkey.
                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                if (game.hasPendingDecision()) {
                    // The targeted trigger asks for its single legal target.
                    game.selectTargets(listOf(hog))
                }
                game.resolveStack()

                withClue("Hog-Monkey has a +1/+1 counter, so the trigger grants it menace until end of turn") {
                    game.state.projectedState.hasKeyword(hog, Keyword.MENACE) shouldBe true
                }
            }
        }
    }
}
