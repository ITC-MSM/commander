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
 * Scenario tests for Mnemonic Sliver.
 *
 * Mnemonic Sliver ({2}{U}): Creature — Sliver, 2/2
 * "All Slivers have '{2}, Sacrifice this permanent: Draw a card.'"
 */
class MnemonicSliverScenarioTest : ScenarioTestBase() {

    init {
        context("Mnemonic Sliver grants the sacrifice-to-draw ability to Slivers") {

            test("another Sliver gains the granted draw ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mnemonic Sliver")
                    .withCardOnBattlefield(1, "Blade Sliver")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bladeSliver = game.findPermanent("Blade Sliver")!!
                val drawAction = game.getLegalActions(1).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == bladeSliver
                }
                withClue("Blade Sliver should have the sacrifice-to-draw ability from Mnemonic Sliver") {
                    drawAction shouldNotBe null
                }
            }

            test("activating sacrifices the Sliver and draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Mnemonic Sliver")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val def = cardRegistry.getCard("Mnemonic Sliver")!!
                val abilityId = def.staticAbilities
                    .filterIsInstance<GrantActivatedAbility>().first().ability.id
                val mnemonic = game.findPermanent("Mnemonic Sliver")!!
                val handBefore = game.handSize(1)

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = mnemonic, abilityId = abilityId)
                )
                withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                withClue("Mnemonic Sliver should have been sacrificed") {
                    game.findPermanent("Mnemonic Sliver") shouldBe null
                }
                withClue("Controller should have drawn a card") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }
        }
    }
}
