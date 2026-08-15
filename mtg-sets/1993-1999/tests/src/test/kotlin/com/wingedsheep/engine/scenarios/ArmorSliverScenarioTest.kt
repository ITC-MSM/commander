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
 * Scenario tests for Armor Sliver.
 *
 * Armor Sliver ({2}{W}): Creature — Sliver, 2/2
 * "All Sliver creatures have '{2}: This creature gets +0/+1 until end of turn.'"
 */
class ArmorSliverScenarioTest : ScenarioTestBase() {

    init {
        context("Armor Sliver grants the toughness-pump ability to Slivers") {

            test("another Sliver gains the granted activated ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Armor Sliver")
                    .withCardOnBattlefield(1, "Blade Sliver")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bladeSliver = game.findPermanent("Blade Sliver")!!
                val pumpAction = game.getLegalActions(1).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == bladeSliver &&
                        it.description.contains("+0/+1")
                }
                withClue("Blade Sliver should have the +0/+1 ability from Armor Sliver") {
                    pumpAction shouldNotBe null
                }
            }

            test("granted ability pumps toughness") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Armor Sliver")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val def = cardRegistry.getCard("Armor Sliver")!!
                val abilityId = def.staticAbilities
                    .filterIsInstance<GrantActivatedAbility>().first().ability.id
                val armor = game.findPermanent("Armor Sliver")!!

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = armor, abilityId = abilityId)
                )
                withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                val card = game.getClientState(1).cards.values.first { it.name == "Armor Sliver" }
                withClue("Armor Sliver should be 2/3 after +0/+1") {
                    card.power shouldBe 2
                    card.toughness shouldBe 3
                }
            }

            test("non-Sliver creatures do not get the ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Armor Sliver")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val grizzly = game.findPermanent("Grizzly Bears")!!
                val pumpAction = game.getLegalActions(1).find {
                    it.actionType == "ActivateAbility" &&
                        (it.action as? ActivateAbility)?.sourceId == grizzly &&
                        it.description.contains("+0/+1")
                }
                withClue("Grizzly Bears should NOT have the ability") { pumpAction shouldBe null }
            }
        }
    }
}
