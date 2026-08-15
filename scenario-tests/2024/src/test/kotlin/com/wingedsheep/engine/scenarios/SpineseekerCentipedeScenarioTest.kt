package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Spineseeker Centipede (DSK #199) — {2}{G} 2/1 Creature — Insect.
 *
 * "When this creature enters, search your library for a basic land card, reveal it, put it into
 *  your hand, then shuffle.
 *  Delirium — This creature gets +1/+2 and has vigilance as long as there are four or more card
 *  types among cards in your graveyard."
 *
 * Exercises the new [Conditions.Delirium] condition (four or more card types among cards in your
 * graveyard, via the DISTINCT_TYPES aggregation over the graveyard zone) gating a +1/+2 self-buff
 * and a vigilance grant.
 */
class SpineseekerCentipedeScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Spineseeker Centipede — Delirium self-buff") {

            test("with fewer than four card types in graveyard, it is a vanilla 2/1 with no vigilance") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Spineseeker Centipede")
                    // Only three card types: creature, instant, sorcery.
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withCardInGraveyard(1, "Doom Blade")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val centipede = game.findPermanent("Spineseeker Centipede")!!
                val projected = stateProjector.project(game.state)

                withClue("Delirium not active — base 2/1") {
                    projected.getPower(centipede) shouldBe 2
                    projected.getToughness(centipede) shouldBe 1
                }
                withClue("Delirium not active — no vigilance") {
                    projected.hasKeyword(centipede, Keyword.VIGILANCE) shouldBe false
                }
            }

            test("with four or more card types in graveyard, it gets +1/+2 and vigilance") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Spineseeker Centipede")
                    // Four card types: creature, instant, sorcery, enchantment.
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withCardInGraveyard(1, "Doom Blade")
                    .withCardInGraveyard(1, "Test Enchantment")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val centipede = game.findPermanent("Spineseeker Centipede")!!
                val projected = stateProjector.project(game.state)

                withClue("Delirium active — 2/1 + 1/2 = 3/3") {
                    projected.getPower(centipede) shouldBe 3
                    projected.getToughness(centipede) shouldBe 3
                }
                withClue("Delirium active — has vigilance") {
                    projected.hasKeyword(centipede, Keyword.VIGILANCE) shouldBe true
                }
            }

            test("cards in the opponent's graveyard do not count toward your Delirium") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Spineseeker Centipede")
                    // Four types, but in the OPPONENT's graveyard.
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInGraveyard(2, "Lightning Bolt")
                    .withCardInGraveyard(2, "Doom Blade")
                    .withCardInGraveyard(2, "Test Enchantment")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val centipede = game.findPermanent("Spineseeker Centipede")!!
                val projected = stateProjector.project(game.state)

                projected.getPower(centipede) shouldBe 2
                projected.getToughness(centipede) shouldBe 1
                projected.hasKeyword(centipede, Keyword.VIGILANCE) shouldBe false
            }
        }
    }
}
