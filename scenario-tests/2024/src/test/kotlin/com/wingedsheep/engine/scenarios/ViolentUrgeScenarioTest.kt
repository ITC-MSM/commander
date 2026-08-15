package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Violent Urge (DSK #164) — {R} Instant.
 *
 * "Target creature gets +1/+0 and gains first strike until end of turn.
 *  Delirium — If there are four or more card types among cards in your graveyard, that creature
 *  gains double strike until end of turn."
 *
 * Exercises the [com.wingedsheep.sdk.dsl.Conditions.Delirium] gate on a resolution-time
 * [com.wingedsheep.sdk.scripting.effects.ConditionalEffect] that grants an extra keyword to the
 * same targeted creature.
 */
class ViolentUrgeScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Violent Urge — pump + first strike, Delirium double strike") {

            test("without Delirium: +1/+0 and first strike, no double strike") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Violent Urge")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    // Only three card types in graveyard.
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withCardInGraveyard(1, "Doom Blade")
                    .withCardInGraveyard(1, "Test Enchantment")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cast = game.castSpell(1, "Violent Urge", bears)
                withClue("cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val p = projector.project(game.state)
                withClue("base 2/2 + 1/0 = 3/2") {
                    p.getPower(bears) shouldBe 3
                    p.getToughness(bears) shouldBe 2
                }
                p.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe true
                withClue("Delirium not active — no double strike") {
                    p.hasKeyword(bears, Keyword.DOUBLE_STRIKE) shouldBe false
                }
            }

            test("with Delirium (four card types): also gains double strike") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Violent Urge")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    // Four card types: creature, instant, sorcery, enchantment.
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withCardInGraveyard(1, "Doom Blade")
                    .withCardInGraveyard(1, "Test Enchantment")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cast = game.castSpell(1, "Violent Urge", bears)
                withClue("cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val p = projector.project(game.state)
                withClue("base 2/2 + 1/0 = 3/2") {
                    p.getPower(bears) shouldBe 3
                    p.getToughness(bears) shouldBe 2
                }
                p.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe true
                withClue("Delirium active — double strike granted") {
                    p.hasKeyword(bears, Keyword.DOUBLE_STRIKE) shouldBe true
                }
            }
        }
    }
}
