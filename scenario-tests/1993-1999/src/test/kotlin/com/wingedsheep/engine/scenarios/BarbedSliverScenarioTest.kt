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
 * Scenario tests for Barbed Sliver.
 *
 * Barbed Sliver ({2}{R}): Creature — Sliver, 2/2
 * "All Sliver creatures have '{2}: This creature gets +1/+0 until end of turn.'"
 */
class BarbedSliverScenarioTest : ScenarioTestBase() {

    init {
        context("Barbed Sliver grants the power-pump ability to Slivers") {

            test("another Sliver gains the granted activated ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Barbed Sliver")
                    .withCardOnBattlefield(1, "Blade Sliver")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bladeSliver = game.findPermanent("Blade Sliver")!!
                val pumpAction = game.getLegalActions(1).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == bladeSliver &&
                        it.description.contains("+1/+0")
                }
                withClue("Blade Sliver should have the +1/+0 ability from Barbed Sliver") {
                    pumpAction shouldNotBe null
                }
            }

            test("granted ability pumps power") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Barbed Sliver")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val def = cardRegistry.getCard("Barbed Sliver")!!
                val abilityId = def.staticAbilities
                    .filterIsInstance<GrantActivatedAbility>().first().ability.id
                val barbed = game.findPermanent("Barbed Sliver")!!

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = barbed, abilityId = abilityId)
                )
                withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                val card = game.getClientState(1).cards.values.first { it.name == "Barbed Sliver" }
                withClue("Barbed Sliver should be 3/2 after +1/+0") {
                    card.power shouldBe 3
                    card.toughness shouldBe 2
                }
            }
        }
    }
}
