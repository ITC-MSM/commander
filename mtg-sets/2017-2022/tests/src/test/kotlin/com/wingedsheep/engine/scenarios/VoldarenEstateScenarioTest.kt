package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Voldaren Estate (VOW #267) — Land.
 *
 *   {T}: Add {C}.
 *   {T}, Pay 1 life: Add one mana of any color. Spend this mana only to cast a Vampire spell.
 *   {5}, {T}: Create a Blood token. This ability costs {1} less to activate for each Vampire you
 *   control.
 *
 * The first ability is a plain colorless mana ability. The Blood ability's generic cost shrinks by
 * the controller's Vampire count — with two Vampires out it costs {3}, {T}, so three lands cover it.
 */
class VoldarenEstateScenarioTest : ScenarioTestBase() {

    init {
        context("Voldaren Estate") {

            test("{T}: Add {C} fills the pool with one colorless mana") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Voldaren Estate", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val estate = game.findPermanent("Voldaren Estate")!!
                val colorlessMana = cardRegistry.getCard("Voldaren Estate")!!.activatedAbilities[0].id

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = estate,
                        abilityId = colorlessMana,
                    )
                )
                withClue("Activating the {T}: Add {C} ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                withClue("The mana ability adds one colorless mana") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.colorless shouldBe 1
                }
            }

            test("{5}, {T}: Create a Blood token, reduced by {1} per Vampire you control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Voldaren Estate", summoningSickness = false)
                    // Two Vampires → the {5} generic shrinks to {3}.
                    .withCardOnBattlefield(1, "Bloodtithe Harvester", summoningSickness = false)
                    .withCardOnBattlefield(1, "Dominating Vampire", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 3) // exactly the reduced {3}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val estate = game.findPermanent("Voldaren Estate")!!
                val bloodAbility = cardRegistry.getCard("Voldaren Estate")!!.activatedAbilities[2].id

                val bloodBefore = game.findPermanents("Blood").size

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = estate,
                        abilityId = bloodAbility,
                    )
                )
                withClue("Activating the reduced Blood ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("With two Vampires, {3} + tap pays the ability and one Blood token is created") {
                    (game.findPermanents("Blood").size - bloodBefore) shouldBe 1
                }
            }
        }
    }
}
