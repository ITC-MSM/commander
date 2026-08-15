package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Patchwork Beastie (DSK #195) — {G} 3/3 Artifact Creature — Beast.
 *
 * "Delirium — This creature can't attack or block unless there are four or more card types
 *  among cards in your graveyard.
 *  At the beginning of your upkeep, you may mill a card."
 *
 * Exercises [com.wingedsheep.sdk.scripting.CantAttackUnless] + [CantBlockUnless] both gated on
 * [com.wingedsheep.sdk.dsl.Conditions.Delirium] (four or more card types among cards in your
 * graveyard), plus an optional upkeep mill.
 */
class PatchworkBeastieScenarioTest : ScenarioTestBase() {

    init {
        context("Patchwork Beastie — Delirium attack restriction") {

            test("cannot attack with fewer than four card types in graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Patchwork Beastie", summoningSickness = false)
                    // Only three card types: creature, instant, sorcery.
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withCardInGraveyard(1, "Doom Blade")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                withClue("Delirium not active — the attack must be rejected") {
                    game.declareAttackers(mapOf("Patchwork Beastie" to 2)).error shouldNotBe null
                }
            }

            test("can attack with four or more card types in graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Patchwork Beastie", summoningSickness = false)
                    // Four card types: creature, instant, sorcery, enchantment.
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withCardInGraveyard(1, "Doom Blade")
                    .withCardInGraveyard(1, "Test Enchantment")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                withClue("Delirium active — the attack is legal") {
                    game.declareAttackers(mapOf("Patchwork Beastie" to 2)).error shouldBe null
                }
            }
        }

        context("Patchwork Beastie — Delirium block restriction") {

            test("cannot block with fewer than four card types in graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Patchwork Beastie", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    // Blocker's controller (P2) has only three card types.
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInGraveyard(2, "Lightning Bolt")
                    .withCardInGraveyard(2, "Doom Blade")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("Delirium not active — the block must be rejected") {
                    game.declareBlockers(mapOf("Patchwork Beastie" to listOf("Grizzly Bears"))).error shouldNotBe null
                }
            }

            test("can block with four or more card types in graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Patchwork Beastie", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInGraveyard(2, "Lightning Bolt")
                    .withCardInGraveyard(2, "Doom Blade")
                    .withCardInGraveyard(2, "Test Enchantment")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("Delirium active — the block is legal") {
                    game.declareBlockers(mapOf("Patchwork Beastie" to listOf("Grizzly Bears"))).error shouldBe null
                }
            }
        }
    }
}
