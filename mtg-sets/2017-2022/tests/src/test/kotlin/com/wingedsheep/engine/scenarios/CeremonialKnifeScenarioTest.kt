package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ceremonial Knife (VOW #254) — {1} Artifact — Equipment
 *
 *   Equipped creature gets +1/+0 and has "Whenever this creature deals combat damage, create a
 *   Blood token."
 *   Equip {2}
 *
 * The stat modifier is ordinary; the interesting half is the *granted* trigger. Two things it must
 * get right: the granted ability fires for the creature carrying it (not for the Equipment), and
 * it fires on combat damage to **anything** — the printed line names no recipient, so damage to a
 * blocking creature counts just as much as damage to a player.
 */
class CeremonialKnifeScenarioTest : ScenarioTestBase() {

    private fun bloodTokens(game: TestGame, playerNumber: Int): Int {
        val playerId = if (playerNumber == 1) game.player1Id else game.player2Id
        return game.state.getBattlefield().count { id ->
            game.state.getEntity(id)?.get<CardComponent>()?.name == "Blood" &&
                game.state.getEntity(id)?.get<ControllerComponent>()?.playerId == playerId
        }
    }

    private fun advanceThroughCombatDamage(game: TestGame) {
        game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
        var iterations = 0
        while (game.state.pendingDecision == null && game.state.stack.isNotEmpty() && iterations++ < 20) {
            game.passPriority()
        }
    }

    init {
        context("Ceremonial Knife — +1/+0 and a granted combat-damage trigger") {

            test("equipped creature is +1/+0") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Ceremonial Knife", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("base 2/2 + 1/0 = 3/2") {
                    game.state.projectedState.getPower(bears) shouldBe 3
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
            }

            test("unblocked combat damage to a player makes a Blood token for the creature's controller") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Ceremonial Knife", "Grizzly Bears")
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                advanceThroughCombatDamage(game)

                withClue("the 3/2 connects for 3") {
                    game.getLifeTotal(2) shouldBe 17
                }
                withClue("the granted trigger creates one Blood token") {
                    bloodTokens(game, 1) shouldBe 1
                }
            }

            test("damage dealt to a blocker still triggers — the line names no recipient") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Ceremonial Knife", "Grizzly Bears")
                    .withCardOnBattlefield(2, "Wall of Wood", summoningSickness = false)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Wall of Wood" to listOf("Grizzly Bears"))).error shouldBe null
                advanceThroughCombatDamage(game)

                withClue("no damage reached the player") {
                    game.getLifeTotal(2) shouldBe 20
                }
                withClue("but combat damage was still dealt, so the Blood token is created") {
                    bloodTokens(game, 1) shouldBe 1
                }
            }

            test("an unequipped creature has no trigger") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Ceremonial Knife")
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                advanceThroughCombatDamage(game)

                withClue("the Knife on the battlefield but unattached grants nothing") {
                    game.getLifeTotal(2) shouldBe 18
                    bloodTokens(game, 1) shouldBe 0
                }
            }
        }
    }
}
