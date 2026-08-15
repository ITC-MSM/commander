package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tla.cards.BitterWork
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Bitter Work (TLA #210).
 *
 * {1}{R}{G} Enchantment.
 * Whenever you attack a player with one or more creatures with power 4 or greater, draw a card.
 * Exhaust — {4}: Earthbend 4. Activate only during your turn.
 */
class BitterWorkScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(BitterWork)

        context("Bitter Work") {

            test("exhaust earthbends a target land for four counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Bitter Work")
                    .withLandsOnBattlefield(1, "Mountain", 4) // pays {4}
                    .withLandsOnBattlefield(1, "Forest", 1)   // the earthbend target
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bitterWork = game.findPermanent("Bitter Work")!!
                val forest = game.findPermanent("Forest")!!
                val abilityId = cardRegistry.getCard("Bitter Work")!!.script.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(
                        game.player1Id, bitterWork, abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, forest))
                    )
                ).error shouldBe null
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                val projected = game.state.projectedState
                withClue("the targeted Forest is earthbent into a creature-land") {
                    projected.hasType(forest, "CREATURE") shouldBe true
                    projected.hasType(forest, "LAND") shouldBe true
                }
                withClue("earthbend 4 puts four +1/+1 counters on the land") {
                    game.state.getEntity(forest)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 4
                }
            }

            test("attacking with a power-4 creature draws a card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Bitter Work")
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false) // 3/3 — too small
                    .withCardOnBattlefield(1, "Serra Angel", summoningSickness = false) // 4/4 — triggers
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                val handBefore = game.handSize(1)
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Serra Angel" to 2)).error shouldBe null
                game.resolveStack()

                withClue("attacking with a power-4 creature draws exactly one card") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }
        }
    }
}
