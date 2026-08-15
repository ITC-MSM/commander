package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tla.cards.RebelliousCaptives
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Rebellious Captives (TLA #191).
 *
 * {1}{G} 2/2 Human Peasant Ally.
 * Exhaust — {6}: Put two +1/+1 counters on this creature, then earthbend 2.
 */
class RebelliousCaptivesScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(RebelliousCaptives)

        context("Rebellious Captives") {

            test("exhaust adds two counters to itself, then earthbends a target land") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Rebellious Captives", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 6) // pays {6}
                    .withLandsOnBattlefield(1, "Forest", 1)   // the earthbend target
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val captives = game.findPermanent("Rebellious Captives")!!
                val forest = game.findPermanent("Forest")!!
                val abilityId = cardRegistry.getCard("Rebellious Captives")!!.script.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(
                        game.player1Id, captives, abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, forest))
                    )
                ).error shouldBe null
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("two +1/+1 counters on Rebellious Captives itself") {
                    game.state.getEntity(captives)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }

                val projected = game.state.projectedState
                withClue("the targeted Forest is earthbent into a creature-land") {
                    projected.hasType(forest, "LAND") shouldBe true
                    projected.hasType(forest, "CREATURE") shouldBe true
                }
                withClue("earthbend 2 puts two +1/+1 counters on the land") {
                    game.state.getEntity(forest)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }
            }
        }
    }
}
