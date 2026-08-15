package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Bulk Up (FDN #80) — {1}{R} Instant.
 *
 * "Double target creature's power until end of turn."
 *
 * Verifies the layer-7c +N/+0 doubling: a 3/3 becomes 6/3 (power doubled, toughness untouched),
 * and the bonus is locked at resolution rather than feeding back on itself.
 */
class BulkUpScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Ogre",
                manaCost = ManaCost.parse("{2}{R}"),
                subtypes = setOf(Subtype("Ogre")),
                power = 3,
                toughness = 3
            )
        )

        context("Bulk Up") {

            test("doubles the target creature's power, leaving toughness unchanged") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Test Ogre", summoningSickness = false)
                    .withCardInHand(1, "Bulk Up")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ogre = game.findPermanent("Test Ogre")!!
                val cast = game.castSpell(1, "Bulk Up", targetId = ogre)
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val card = game.getClientState(1).cards.values.first { it.name == "Test Ogre" }
                withClue("Test Ogre should be 6/3 after doubling its power") {
                    card.power shouldBe 6
                    card.toughness shouldBe 3
                }
            }
        }
    }
}
