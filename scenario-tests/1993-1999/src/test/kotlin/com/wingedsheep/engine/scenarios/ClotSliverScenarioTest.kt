package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Clot Sliver.
 *
 * Clot Sliver ({1}{B}): Creature — Sliver, 1/1
 * "All Slivers have '{2}: Regenerate this permanent.'"
 */
class ClotSliverScenarioTest : ScenarioTestBase() {

    init {
        context("Clot Sliver grants the regenerate ability to Slivers") {

            test("another Sliver gains the granted regenerate ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Clot Sliver")
                    .withCardOnBattlefield(1, "Blade Sliver")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bladeSliver = game.findPermanent("Blade Sliver")!!
                val regenAction = game.getLegalActions(1).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == bladeSliver &&
                        it.description.contains("Regenerate", ignoreCase = true)
                }
                withClue("Blade Sliver should have the regenerate ability from Clot Sliver") {
                    regenAction shouldNotBe null
                }
            }

            test("activating the granted ability creates a regeneration shield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Clot Sliver")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val def = cardRegistry.getCard("Clot Sliver")!!
                val abilityId = def.staticAbilities
                    .filterIsInstance<GrantActivatedAbility>().first().ability.id
                val clot = game.findPermanent("Clot Sliver")!!

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = clot, abilityId = abilityId)
                )
                withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                val card = game.getClientState(1).cards.values.first { it.name == "Clot Sliver" }
                withClue("Clot Sliver should have a regeneration shield") {
                    card.activeEffects.any { it.name.startsWith("Regen") } shouldBe true
                }
            }
        }
    }
}
