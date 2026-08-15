package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Diamond Pick-Axe (LCI #143) — {R} Artifact — Equipment.
 *
 * Indestructible (Effects that say "destroy" don't destroy this Equipment.)
 * Equipped creature gets +1/+1 and has "Whenever this creature attacks, create a Treasure token."
 * Equip {2}
 *
 * Tests:
 *  1. Equipped creature gets +1/+1 from the static ability.
 *  2. Attacking with the equipped creature creates a Treasure token.
 *  3. The Pick-Axe itself has Indestructible (projected keyword check).
 */
class DiamondPickAxeScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Diamond Pick-Axe") {

            test("equipped creature gets +1/+1") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Diamond Pick-Axe", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = projector.project(game.state)

                withClue("Grizzly Bears base 2/2 + Pick-Axe +1/+1 = 3/3") {
                    projected.getPower(bears) shouldBe 3
                    projected.getToughness(bears) shouldBe 3
                }
            }

            test("Diamond Pick-Axe itself is indestructible") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Diamond Pick-Axe")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val pickAxe = game.findPermanent("Diamond Pick-Axe")!!
                withClue("Diamond Pick-Axe has indestructible printed on itself") {
                    projector.project(game.state).hasKeyword(pickAxe, Keyword.INDESTRUCTIBLE) shouldBe true
                }
            }

            test("attacking with the equipped creature creates a Treasure token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Diamond Pick-Axe", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("No Treasure exists before attacking") {
                    game.findAllPermanents("Treasure").size shouldBe 0
                }

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("Attacking with the equipped creature triggers and creates exactly one Treasure") {
                    game.findAllPermanents("Treasure").size shouldBe 1
                }
            }

            test("unequipped creature attacking does NOT create a Treasure token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(1, "Diamond Pick-Axe")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.resolveStack()

                withClue("No Treasure — Pick-Axe is not equipped, grant is inactive") {
                    game.findAllPermanents("Treasure").size shouldBe 0
                }
            }
        }
    }
}
