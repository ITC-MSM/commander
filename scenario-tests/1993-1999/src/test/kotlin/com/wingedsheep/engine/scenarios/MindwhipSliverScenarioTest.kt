package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Mindwhip Sliver.
 *
 * Mindwhip Sliver ({2}{B}): Creature — Sliver, 2/2
 * "All Slivers have '{2}, Sacrifice this permanent: Target player discards a card at
 *  random. Activate only as a sorcery.'"
 */
class MindwhipSliverScenarioTest : ScenarioTestBase() {

    init {
        context("Mindwhip Sliver grants the sacrifice-to-discard ability to Slivers") {

            test("another Sliver gains the granted ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mindwhip Sliver")
                    .withCardOnBattlefield(1, "Blade Sliver")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bladeSliver = game.findPermanent("Blade Sliver")!!
                val action = game.getLegalActions(1).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == bladeSliver
                }
                withClue("Blade Sliver should have the sacrifice-to-discard ability from Mindwhip Sliver") {
                    action shouldNotBe null
                }
            }

            test("activating sacrifices the Sliver and the target player discards a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mindwhip Sliver")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardInHand(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val def = cardRegistry.getCard("Mindwhip Sliver")!!
                val abilityId = def.staticAbilities
                    .filterIsInstance<GrantActivatedAbility>().first().ability.id
                val mindwhip = game.findPermanent("Mindwhip Sliver")!!
                val opponentHandBefore = game.handSize(2)

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = mindwhip,
                        abilityId = abilityId,
                        targets = listOf(ChosenTarget.Player(game.player2Id))
                    )
                )
                withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                withClue("Mindwhip Sliver should have been sacrificed") {
                    game.findPermanent("Mindwhip Sliver") shouldBe null
                }
                withClue("Target player should have discarded a card at random") {
                    game.handSize(2) shouldBe opponentHandBefore - 1
                }
            }
        }
    }
}
