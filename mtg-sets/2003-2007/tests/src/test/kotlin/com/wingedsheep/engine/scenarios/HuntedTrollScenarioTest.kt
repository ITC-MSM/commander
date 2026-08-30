package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario test for Hunted Troll (RAV #170) — {2}{G}{G} Creature — Troll Warrior 8/4.
 *
 *   When this creature enters, target opponent creates four 1/1 blue Faerie creature tokens with
 *   flying.
 *   {G}: Regenerate this creature.
 *
 * The green member of the cycle is the only one that pairs the enters-drawback with an activated
 * ability, so this covers both halves: the four flying Faeries landing under the opponent's
 * control, and the regeneration shield the Troll uses to survive what they buy time for.
 */
class HuntedTrollScenarioTest : ScenarioTestBase() {

    private val regenerateAbilityId =
        cardRegistry.getCard("Hunted Troll")!!.activatedAbilities.first().id

    init {
        context("Hunted Troll") {

            test("four 1/1 flying Faeries enter under the targeted opponent's control") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardInHand(1, "Hunted Troll")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hunted Troll").isSuccess shouldBe true
                game.resolveStack()

                val troll = game.findPermanent("Hunted Troll")!!
                withClue("Hunted Troll is an 8/4") {
                    game.state.projectedState.getPower(troll) shouldBe 8
                    game.state.projectedState.getToughness(troll) shouldBe 4
                }

                val faeries = game.findPermanents("Faerie Token")
                withClue("All four Faeries are created") { faeries shouldHaveSize 4 }

                withClue("Every Faerie is a 1/1 flier under the opponent's control") {
                    faeries.forEach { faerie ->
                        game.state.getBattlefield(game.player2Id).contains(faerie) shouldBe true
                        game.state.projectedState.getPower(faerie) shouldBe 1
                        game.state.projectedState.getToughness(faerie) shouldBe 1
                        game.state.projectedState.hasKeyword(faerie, Keyword.FLYING) shouldBe true
                    }
                }
            }

            test("{G} regeneration replaces the next destruction this turn") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Hunted Troll")
                    .withCardInHand(1, "Rend Flesh")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val troll = game.findPermanent("Hunted Troll")!!

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = troll,
                        abilityId = regenerateAbilityId,
                    )
                )
                withClue("Activating regeneration should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                // Rend Flesh destroys a non-Spirit creature and carries no "can't be
                // regenerated" rider, so the shield is the only thing standing between the Troll
                // and the graveyard.
                game.castSpell(1, "Rend Flesh", troll).isSuccess shouldBe true
                game.resolveStack()
                game.checkStateBasedActions()

                withClue("The regeneration shield replaces the destruction (CR 701.15)") {
                    game.isOnBattlefield("Hunted Troll") shouldBe true
                    game.isInGraveyard(1, "Hunted Troll") shouldBe false
                }
                withClue("Regenerating taps the creature (CR 701.15a)") {
                    game.state.getEntity(troll)?.get<TappedComponent>() shouldNotBe null
                }
            }
        }
    }
}
