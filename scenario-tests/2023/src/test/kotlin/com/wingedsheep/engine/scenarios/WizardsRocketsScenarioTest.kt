package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ColorChosenResponse
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Wizard's Rockets (LTR #252) — Artifact.
 *
 * "{X}, {T}, Sacrifice this artifact: Add X mana in any combination of colors.
 *  When this artifact is put into a graveyard from the battlefield, draw a card."
 *
 * The mana ability resolves off the stack, but sacrificing the source to pay its cost fires
 * the dies/leaves-the-battlefield triggered ability ("draw a card"), which still uses the stack.
 *
 * Regressions covered:
 *  1. ActivateAbilityHandler's mana-ability path returned early without ever running trigger
 *     detection on the cost-payment events, so the sacrifice's draw trigger was dropped on the
 *     floor — including when the mana effect itself paused for a color choice.
 *  2. The mana-ability effect context passed `xValue = null`, so `DynamicAmount.XValue` resolved
 *     to 0 and the ability produced no mana regardless of X.
 */
class WizardsRocketsScenarioTest : ScenarioTestBase() {

    private val manaAbilityId = cardRegistry.getCard("Wizard's Rockets")!!.activatedAbilities[0].id

    init {
        context("Wizard's Rockets sacrifice-as-mana-ability-cost draw trigger") {

            test("activating the mana ability (sacrificing the artifact) draws a card") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wizard's Rockets", tapped = false, summoningSickness = false)
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val rockets = game.findPermanent("Wizard's Rockets")!!

                withClue("Hand starts empty") { game.handSize(1) shouldBe 0 }

                // X = 0 keeps the test free of mana sources — the sacrifice (and its trigger)
                // happens regardless of how much mana is produced.
                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = rockets,
                        abilityId = manaAbilityId,
                        xValue = 0
                    )
                )
                withClue("Activating the mana ability should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }

                withClue("Wizard's Rockets should be in the graveyard after being sacrificed") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.GRAVEYARD))
                        .contains(rockets) shouldBe true
                }

                // "Add X mana in any combination of colors" pauses the mana ability for a color
                // choice. The dies trigger must survive that pause (queued beneath the decision).
                if (game.hasPendingDecision()) {
                    game.submitDecision(ColorChosenResponse(game.getPendingDecision()!!.id, Color.RED))
                }

                // The draw trigger went on the stack; resolve it.
                game.resolveStack()

                withClue("The draw-a-card death trigger should have drawn a card") {
                    game.handSize(1) shouldBe 1
                }
            }

            test("paying X=2 lets each mana be colored independently (true combination)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wizard's Rockets", tapped = false, summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val rockets = game.findPermanent("Wizard's Rockets")!!

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = rockets,
                        abilityId = manaAbilityId,
                        xValue = 2
                    )
                )
                withClue("Activating with X=2 should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }

                // "Any combination of colors" prompts once per mana — colour the first RED and
                // the second BLUE to prove they're chosen independently (not one colour ×2).
                val colors = listOf(Color.RED, Color.BLUE)
                var i = 0
                while (game.hasPendingDecision()) {
                    val color = colors.getOrElse(i++) { Color.RED }
                    game.submitDecision(ColorChosenResponse(game.getPendingDecision()!!.id, color))
                }

                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("X=2 should add one RED and one BLUE (a true colour combination)") {
                    pool?.red shouldBe 1
                    pool?.blue shouldBe 1
                }

                game.resolveStack()

                withClue("The sacrifice's draw trigger still fires when X mana is produced") {
                    game.handSize(1) shouldBe 1
                }
            }

            test("X=2 may also be taken as two of the same colour") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Wizard's Rockets", tapped = false, summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val rockets = game.findPermanent("Wizard's Rockets")!!
                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = rockets,
                        abilityId = manaAbilityId,
                        xValue = 2
                    )
                )
                withClue("Activating with X=2 should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                while (game.hasPendingDecision()) {
                    game.submitDecision(ColorChosenResponse(game.getPendingDecision()!!.id, Color.GREEN))
                }

                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("Both mana coloured green → two green") {
                    pool?.green shouldBe 2
                }
            }
        }
    }
}
