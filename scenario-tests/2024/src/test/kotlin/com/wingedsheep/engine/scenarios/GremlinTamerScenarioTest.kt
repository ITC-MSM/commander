package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Gremlin Tamer (DSK #215).
 *
 * Gremlin Tamer — {W}{U} Creature — Human Scout, 2/2
 *   "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a
 *    Room, create a 1/1 red Gremlin creature token."
 *
 * Verifies the enchantment-enters Eerie trigger creates a 1/1 red Gremlin, and that an
 * opponent's enchantment entering does NOT trigger it (the trigger is "an enchantment you control").
 */
class GremlinTamerScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private fun TestGame.assertGremlinToken(tokenId: com.wingedsheep.sdk.model.EntityId) {
        val card = state.getEntity(tokenId)!!.get<CardComponent>()!!
        card.colors shouldBe setOf(Color.RED)
        withClue("Gremlin token must have the Gremlin subtype") {
            card.typeLine.subtypes.any { it.value == "Gremlin" } shouldBe true
        }
        val projected = projector.project(state)
        projected.getPower(tokenId) shouldBe 1
        projected.getToughness(tokenId) shouldBe 1
    }

    init {
        context("Gremlin Tamer — Eerie (enchantment enters)") {

            test("an enchantment you control entering creates a 1/1 red Gremlin token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Gremlin Tamer")
                    .withCardInHand(1, "Test Enchantment") // {1}{W}
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Test Enchantment")
                withClue("Casting Test Enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val token = game.findPermanent("Gremlin Token").shouldNotBeNull()
                game.assertGremlinToken(token)
            }

            test("an opponent's enchantment entering does NOT create a Gremlin token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Gremlin Tamer")
                    .withCardInHand(2, "Test Enchantment") // {1}{W}, controlled by the opponent
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(2, "Test Enchantment")
                withClue("Opponent casting Test Enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("The Tamer's controller gets no token — the enchantment isn't theirs") {
                    game.findPermanent("Gremlin Token") shouldBe null
                }
            }
        }
    }
}
