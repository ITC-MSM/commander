package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fin.cards.RideTheShoopuf
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ride the Shoopuf {1}{G} Enchantment (FIN #197).
 *
 * Landfall — Whenever a land you control enters, put a +1/+1 counter on target creature you control.
 * {5}{G}{G}: This enchantment becomes a 7/7 Beast creature in addition to its other types.
 */
class RideTheShoopufScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(listOf(RideTheShoopuf))

        context("Ride the Shoopuf") {

            test("landfall puts a +1/+1 counter on a target creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ride the Shoopuf")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("Grizzly Bears starts with no +1/+1 counters") {
                    val counters = game.state.getEntity(bears)?.get<CountersComponent>()?.counters ?: emptyMap()
                    counters[CounterType.PLUS_ONE_PLUS_ONE] shouldBe null
                }

                val forest = game.findCardsInHand(1, "Forest").first()
                game.execute(PlayLand(game.player1Id, forest))

                // Landfall trigger goes on the stack and asks for its "target creature you control".
                if (game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(bears))
                }
                game.resolveStack()

                withClue("Landfall puts one +1/+1 counter on the targeted creature") {
                    val counters = game.state.getEntity(bears)?.get<CountersComponent>()?.counters ?: emptyMap()
                    counters[CounterType.PLUS_ONE_PLUS_ONE] shouldBe 1
                }
            }

            test("{5}{G}{G} turns it into a 7/7 Beast creature that is still an Enchantment") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ride the Shoopuf")
                    .withLandsOnBattlefield(1, "Forest", 7) // pays {5}{G}{G}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val shoopuf = game.findPermanent("Ride the Shoopuf")!!

                withClue("starts as a noncreature enchantment") {
                    game.state.projectedState.isCreature(shoopuf) shouldBe false
                    game.state.projectedState.hasType(shoopuf, "ENCHANTMENT") shouldBe true
                }

                val abilityId = cardRegistry.getCard("Ride the Shoopuf")!!
                    .script.activatedAbilities[0].id

                val activate = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = shoopuf,
                        abilityId = abilityId
                    )
                )
                withClue("activating {5}{G}{G} should succeed: ${activate.error}") {
                    activate.error shouldBe null
                }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                val projected = game.state.projectedState
                withClue("becomes a creature") { projected.isCreature(shoopuf) shouldBe true }
                withClue("7 power") { projected.getPower(shoopuf) shouldBe 7 }
                withClue("7 toughness") { projected.getToughness(shoopuf) shouldBe 7 }
                withClue("is a Beast") { projected.hasSubtype(shoopuf, "Beast") shouldBe true }
                withClue("still an Enchantment (in addition to its other types)") {
                    projected.hasType(shoopuf, "ENCHANTMENT") shouldBe true
                }
            }
        }
    }
}
