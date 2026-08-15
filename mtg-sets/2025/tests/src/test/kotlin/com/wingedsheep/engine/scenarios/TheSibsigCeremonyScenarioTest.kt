package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for The Sibsig Ceremony (TDM #91) — {B}{B}{B} Legendary Enchantment.
 *
 * "Creature spells you cast cost {2} less to cast.
 *  Whenever a creature you control enters, if you cast it, destroy that creature, then create a
 *  2/2 black Zombie Druid creature token."
 *
 * Confirms (1) the creature-spell cost reduction, (2) the "if you cast it" trigger destroying the
 * cast creature and making a Zombie Druid token, and (3) that a creature put onto the battlefield
 * by another effect (a token) does NOT trigger the destroy (the cast-subject intervening-if).
 */
class TheSibsigCeremonyScenarioTest : ScenarioTestBase() {

    init {
        context("The Sibsig Ceremony") {

            test("a cast creature is destroyed and replaced with a 2/2 Zombie Druid token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "The Sibsig Ceremony")
                    .withCardInHand(1, "Hill Giant") // {3}{R} → {2} less = {1}{R}
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // {3}{R} reduced by {2} → {1}{R}, payable with the two Mountains.
                withClue("Hill Giant casts for {2} less (two lands cover {1}{R})") {
                    game.castSpell(1, "Hill Giant").error shouldBe null
                }
                game.resolveStack() // Hill Giant enters → "if you cast it" trigger → resolves

                withClue("The cast creature is destroyed by the trigger") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                    game.isInGraveyard(1, "Hill Giant") shouldBe true
                }
                withClue("A 2/2 black Zombie Druid token is created") {
                    game.findAllPermanents("Zombie Druid Token").size shouldBe 1
                }
            }

            test("a creature put onto the battlefield (not cast) does NOT trigger the destroy") {
                // Reanimate Grizzly Bears with Perennation — it is *put onto* the battlefield, not
                // cast, so The Sibsig Ceremony's "if you cast it" intervening-if is false.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "The Sibsig Ceremony")
                    .withCardInHand(1, "Perennation") // {3}{W}{B}{G} reanimation
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpellTargetingGraveyardCard(1, "Perennation", 1, "Grizzly Bears")
                withClue("Casting Perennation should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // Grizzly Bears was put onto the battlefield, not cast → no destroy, no token.
                withClue("The reanimated (not-cast) creature survives") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("No Zombie Druid token is created for a non-cast enter") {
                    game.findAllPermanents("Zombie Druid Token").size shouldBe 0
                }
            }
        }
    }
}
