package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tests for Serene Heart.
 *
 * Serene Heart: {1}{G}
 * Instant
 * Destroy all Auras.
 *
 * Regression: the card was originally auto-generated as "destroy all permanents",
 * which wiped lands and creatures too. It must only destroy Auras.
 */
class SereneHeartTest : ScenarioTestBase() {

    init {
        context("Serene Heart") {
            test("destroys all Auras but leaves creatures, lands, and non-aura enchantments") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Serene Heart")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    // Opponent's board: a creature wearing an Aura, plus a land and a
                    // non-aura enchantment that must survive.
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(2, "Pacifism", "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Plains", 1)
                    .withCardOnBattlefield(2, "Test Enchantment")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("Pacifism should start attached on the battlefield") {
                    game.isOnBattlefield("Pacifism") shouldBe true
                }

                val castResult = game.castSpell(1, "Serene Heart")
                withClue("Serene Heart should cast successfully: ${castResult.error}") {
                    castResult.error shouldBe null
                }

                game.resolveStack()

                withClue("the Aura (Pacifism) should be destroyed") {
                    game.isOnBattlefield("Pacifism") shouldBe false
                }
                withClue("Pacifism should be in its owner's graveyard") {
                    game.isInGraveyard(2, "Pacifism") shouldBe true
                }

                withClue("the enchanted creature should survive") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("the opponent's land should survive") {
                    game.isOnBattlefield("Plains") shouldBe true
                }
                withClue("a non-aura enchantment should survive") {
                    game.isOnBattlefield("Test Enchantment") shouldBe true
                }
                withClue("the caster's lands should survive") {
                    game.findPermanents("Forest").size shouldBe 2
                }
            }
        }
    }
}
