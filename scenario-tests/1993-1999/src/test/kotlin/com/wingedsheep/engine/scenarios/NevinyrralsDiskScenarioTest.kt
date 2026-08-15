package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Nevinyrral's Disk (LEA #266).
 *
 * {4} Artifact — enters tapped.
 * {1}, {T}: Destroy all artifacts, creatures, and enchantments.
 *
 * Guards against the generated definition's original bug, where the ability only destroyed
 * creatures (a bare `GameObjectFilter.Creature` group) and therefore left every artifact and
 * enchantment — including the Disk itself — untouched. The Disk is an artifact, so a correct
 * "destroy all artifacts, creatures, and enchantments" sweep must destroy it too.
 */
class NevinyrralsDiskScenarioTest : ScenarioTestBase() {

    private val abilityId by lazy {
        cardRegistry.getCard("Nevinyrral's Disk")!!.script.activatedAbilities[0].id
    }

    init {
        context("Nevinyrral's Disk") {

            test("{1}, {T}: destroys all artifacts, creatures, and enchantments — including itself") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Nevinyrral's Disk", summoningSickness = false)
                    // Controller's board: one of each destroyable type.
                    .withCardOnBattlefield(1, "Grizzly Bears")   // creature
                    .withCardOnBattlefield(1, "Rod of Ruin")     // (noncreature) artifact
                    .withCardOnBattlefield(1, "Crusade")         // enchantment
                    // Opponent's board is swept too — the effect is global.
                    .withCardOnBattlefield(2, "Savannah Lions")  // creature
                    .withLandsOnBattlefield(1, "Plains", 1)      // pays the {1}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val disk = game.findPermanent("Nevinyrral's Disk")!!

                withClue("Everything is on the battlefield before activation") {
                    game.isOnBattlefield("Nevinyrral's Disk") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Rod of Ruin") shouldBe true
                    game.isOnBattlefield("Crusade") shouldBe true
                    game.isOnBattlefield("Savannah Lions") shouldBe true
                }

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = disk, abilityId = abilityId)
                ).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("The creature is destroyed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Savannah Lions") shouldBe false
                    game.isInGraveyard(2, "Savannah Lions") shouldBe true
                }

                withClue("The artifact is destroyed") {
                    game.isOnBattlefield("Rod of Ruin") shouldBe false
                    game.isInGraveyard(1, "Rod of Ruin") shouldBe true
                }

                withClue("The enchantment is destroyed") {
                    game.isOnBattlefield("Crusade") shouldBe false
                    game.isInGraveyard(1, "Crusade") shouldBe true
                }

                withClue("The Disk destroys itself — it is an artifact") {
                    game.isOnBattlefield("Nevinyrral's Disk") shouldBe false
                    game.isInGraveyard(1, "Nevinyrral's Disk") shouldBe true
                }

                withClue("The land (not an artifact/creature/enchantment) survives") {
                    game.isOnBattlefield("Plains") shouldBe true
                }
            }
        }
    }
}
