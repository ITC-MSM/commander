package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Hunt for Specimens (STX) — "Create a 1/1 black and green Pest creature token with 'When this
 * token dies, you gain 1 life.' Learn."
 *
 * The Pest is the piece worth testing: it is the first user of the new `PredefinedTokens.Pest`
 * registry entry, and a predefined token's whole point is that the *token* carries the triggered
 * ability rather than the card that minted it. So the interesting assertion is not "a token
 * appeared" but "the token that appeared gains its controller 1 life when it dies".
 *
 * Its colours come from a colour indicator (CR 204) — a token has no mana cost, so black-and-green
 * has to survive the round trip through `colorIdentityOverride` rather than being derived.
 */
class HuntForSpecimensScenarioTest : ScenarioTestBase() {

    init {
        context("the Pest token") {
            test("is a 1/1 black and green Pest") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Hunt for Specimens")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hunt for Specimens").error shouldBe null
                game.resolveStack()
                // Empty hand and empty sideboard: Learn may raise no prompt at all.
                while (game.hasPendingDecision()) game.skipSelection()

                val pest = game.findPermanent("Pest")
                withClue("Hunt for Specimens created a Pest token") { pest shouldNotBe null }
                val pestId = pest!!

                val projected = game.state.projectedState
                withClue("a 1/1 Pest") {
                    projected.getPower(pestId) shouldBe 1
                    projected.getToughness(pestId) shouldBe 1
                    projected.isCreature(pestId) shouldBe true
                    projected.hasSubtype(pestId, "Pest") shouldBe true
                }
                withClue("black AND green, from the colour indicator (CR 204)") {
                    projected.getColors(pestId) shouldBe setOf("BLACK", "GREEN")
                }
            }

            test("gains its controller 1 life when it dies") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Hunt for Specimens")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hunt for Specimens").error shouldBe null
                game.resolveStack()
                while (game.hasPendingDecision()) game.skipSelection()

                val pest = game.findPermanent("Pest")
                withClue("Hunt for Specimens created a Pest token") { pest shouldNotBe null }
                val pestId = pest!!
                val lifeBefore = game.getLifeTotal(1)

                // Shock your own Pest — 2 damage to a 1/1 is lethal.
                game.castSpell(1, "Shock", pestId).error shouldBe null
                game.resolveStack()

                withClue("the Pest died") {
                    game.findPermanent("Pest") shouldBe null
                }
                withClue("its own dies trigger gained you 1 life") {
                    game.getLifeTotal(1) shouldBe lifeBefore + 1
                }
            }
        }
    }
}
