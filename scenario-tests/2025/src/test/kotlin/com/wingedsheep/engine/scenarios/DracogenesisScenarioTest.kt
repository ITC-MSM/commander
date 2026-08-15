package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.MayCastWithoutPayingManaCost
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dracogenesis (TDM #105) and the SDK primitive it introduces:
 * a `spellFilter` on [MayCastWithoutPayingManaCost].
 *
 * Dracogenesis — {6}{R}{R} Enchantment.
 *   "You may cast Dragon spells without paying their mana costs."
 *
 * The free-cast permission is now type-scoped: a Dragon spell may be cast for {0}, but a
 * non-Dragon spell may not, even with Dracogenesis on the battlefield.
 */
class DracogenesisScenarioTest : ScenarioTestBase() {

    private val calculator = CostCalculator(cardRegistry)

    private fun com.wingedsheep.engine.state.GameState.cardInHand(playerId: EntityId, name: String): EntityId =
        getHand(playerId).first { getEntity(it)?.get<CardComponent>()?.name == name }

    init {
        context("Dragon-scoped free cast") {

            test("Dracogenesis grants free-cast permission for a Dragon spell but not a non-Dragon spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Dracogenesis")
                    .withCardInHand(1, "Dirgur Island Dragon")
                    .withCardInHand(1, "Grizzly Bears")
                    .build()
                val me = game.player1Id
                val dragon = game.state.cardInHand(me, "Dirgur Island Dragon")
                val bears = game.state.cardInHand(me, "Grizzly Bears")

                calculator.hasFreeCastPermission(
                    game.state, me, cardRegistry.requireCard("Dirgur Island Dragon")
                ) shouldBe true
                calculator.hasFreeCastPermission(
                    game.state, me, cardRegistry.requireCard("Grizzly Bears")
                ) shouldBe false

                // Free-casting the non-Dragon is rejected outright (empty stack, our main phase —
                // the only obstacle is the Dragon-scoped free-cast filter).
                game.execute(
                    CastSpell(me, bears, useWithoutPayingManaCost = true)
                ).error shouldBe "'Without paying its mana cost' is not available (gate closed or no source on the battlefield)"

                // The Dragon, by contrast, casts for free and lands on the stack.
                game.execute(
                    CastSpell(me, dragon, useWithoutPayingManaCost = true)
                ).error shouldBe null
                game.state.stack.contains(dragon) shouldBe true
            }

            test("without Dracogenesis, even a Dragon spell has no free-cast permission") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Dirgur Island Dragon")
                    .build()
                val me = game.player1Id

                calculator.hasFreeCastPermission(
                    game.state, me, cardRegistry.requireCard("Dirgur Island Dragon")
                ) shouldBe false
            }

            test("the static ability is the controller-only, Dragon-filtered variant") {
                val def = cardRegistry.requireCard("Dracogenesis")
                val ability = def.script.staticAbilities
                    .filterIsInstance<MayCastWithoutPayingManaCost>()
                    .single()
                ability.controllerOnly shouldBe true
                ability.spellFilter shouldBe
                    com.wingedsheep.sdk.scripting.GameObjectFilter.Any.withSubtype("Dragon")
            }
        }
    }
}
