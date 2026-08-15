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
 * Scenario test for Cemetery Recruitment (EMN #83, reprinted FDN #517) — {1}{B} Sorcery.
 *
 * "Return target creature card from your graveyard to your hand. If it's a Zombie card, draw a card."
 *
 * Verifies both the unconditional return and the Zombie-gated bonus draw: returning a Zombie card
 * draws a card, returning a non-Zombie creature card does not.
 */
class CemeteryRecruitmentScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Zombie",
                manaCost = ManaCost.parse("{2}{B}"),
                subtypes = setOf(Subtype.ZOMBIE),
                power = 2,
                toughness = 2
            )
        )
        cardRegistry.register(
            CardDefinition.creature(
                name = "Test Bear",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )

        context("Cemetery Recruitment") {

            test("returning a Zombie card also draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cemetery Recruitment")
                    .withCardInGraveyard(1, "Test Zombie")
                    .withCardInLibrary(1, "Swamp")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val cast = game.castSpellTargetingGraveyardCard(1, "Cemetery Recruitment", 1, "Test Zombie")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Test Zombie should be back in hand") {
                    game.isInHand(1, "Test Zombie") shouldBe true
                }
                // Started with Cemetery Recruitment in hand; it left for the stack (-1), Test Zombie
                // returned (+1), and the Zombie bonus drew the Swamp (+1) => net +1 vs the start.
                withClue("Returning a Zombie should net one extra card (the bonus draw)") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("returning a non-Zombie creature card does not draw") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Cemetery Recruitment")
                    .withCardInGraveyard(1, "Test Bear")
                    .withCardInLibrary(1, "Swamp")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                game.castSpellTargetingGraveyardCard(1, "Cemetery Recruitment", 1, "Test Bear").error shouldBe null
                game.resolveStack()

                withClue("Test Bear should be back in hand") {
                    game.isInHand(1, "Test Bear") shouldBe true
                }
                // Cemetery Recruitment left (-1), Test Bear returned (+1), no bonus draw => same size.
                withClue("Returning a non-Zombie should not draw, so hand size is unchanged") {
                    game.handSize(1) shouldBe handBefore
                }
            }
        }
    }
}
