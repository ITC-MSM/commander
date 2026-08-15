package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Orbital Plunge (EOE) — {3}{R} Sorcery.
 *
 * "Orbital Plunge deals 6 damage to target creature. If excess damage was dealt this way,
 *  create a Lander token."
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.conditions.TargetMarkedDamageExceedsToughness]
 * condition: the conditional Lander payoff fires only when the marked damage left on the
 * creature after resolution STRICTLY exceeds its (projected) toughness — equality is not excess.
 */
class OrbitalPlungeScenarioTest : ScenarioTestBase() {

    // Vanilla 0/6 wall used to pin the equality boundary: 6 damage to toughness 6.
    private val testWall = card("Orbital Plunge Test Wall") {
        manaCost = "{4}"
        typeLine = "Creature — Wall"
        power = 0
        toughness = 6
    }

    init {
        cardRegistry.register(testWall)

        context("Orbital Plunge's excess-damage Lander payoff") {

            test("6 damage to a 2/2 creature is excess → create a Lander") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Orbital Plunge")
                    .withCardOnBattlefield(2, "Grizzly Bears") // 2/2
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val cast = game.castSpell(1, "Orbital Plunge", bears)
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // Grizzly Bears took 6 damage with toughness 2 → 4 excess. Lander created.
                withClue("A Lander token should exist on player 1's battlefield") {
                    game.state.getBattlefield().any { entityId ->
                        val card = game.state.getEntity(entityId)?.get<CardComponent>()
                        card?.name == "Lander"
                    } shouldBe true
                }
            }

            test("6 damage to a 0/6 creature exactly equals toughness → no Lander") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Orbital Plunge")
                    .withCardOnBattlefield(2, "Orbital Plunge Test Wall") // 0/6
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val target = game.findPermanent("Orbital Plunge Test Wall")!!
                game.castSpell(1, "Orbital Plunge", target).error shouldBe null
                game.resolveStack()

                // marked damage (6) == toughness (6) — the condition uses strict-greater,
                // so the Lander payoff must not fire on the equality boundary.
                withClue("No Lander when damage exactly equals toughness (strict-greater)") {
                    game.state.getBattlefield().none { entityId ->
                        val card = game.state.getEntity(entityId)?.get<CardComponent>()
                        card?.name == "Lander"
                    } shouldBe true
                }
            }

            test("6 damage to a 9/9 creature is below lethal → no Lander") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Orbital Plunge")
                    .withCardOnBattlefield(2, "Bygone Colossus") // 9/9
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val target = game.findPermanent("Bygone Colossus")!!
                game.castSpell(1, "Orbital Plunge", target).error shouldBe null
                game.resolveStack()

                withClue("No Lander when damage is less than toughness") {
                    game.state.getBattlefield().none { entityId ->
                        val card = game.state.getEntity(entityId)?.get<CardComponent>()
                        card?.name == "Lander"
                    } shouldBe true
                }
            }
        }
    }
}
