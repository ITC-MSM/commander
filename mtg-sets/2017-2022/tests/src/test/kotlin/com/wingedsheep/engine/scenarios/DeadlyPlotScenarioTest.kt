package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Deadly Plot (J22 #22).
 *
 * {3}{B} Instant
 * "Choose one —
 *  • Destroy target creature or planeswalker.
 *  • Return target Zombie creature card from your graveyard to the battlefield tapped."
 *
 * Covers both modes, and that the reanimation mode is scoped to Zombie creature cards in your
 * own graveyard (a non-Zombie creature card is not a legal target for it).
 */
class DeadlyPlotScenarioTest : ScenarioTestBase() {

    init {
        context("Deadly Plot") {

            test("mode 1 destroys the targeted creature") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Deadly Plot")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpellWithMode(1, "Deadly Plot", modeIndex = 0, targetId = bears)
                    .error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Grizzly Bears") shouldBe false
                game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            }

            test("mode 2 returns a Zombie creature card from your graveyard tapped") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Deadly Plot")
                    .withCardInGraveyard(1, "Zombie Master")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val zombie = game.findCardsInGraveyard(1, "Zombie Master").single()
                val plot = game.findCardsInHand(1, "Deadly Plot").single()
                val target = listOf(ChosenTarget.Card(zombie, game.player1Id, Zone.GRAVEYARD))

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = plot,
                        targets = target,
                        chosenModes = listOf(1),
                        modeTargetsOrdered = listOf(target),
                    )
                ).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Zombie Master") shouldBe true
                val permanent = game.findPermanent("Zombie Master")!!
                (game.state.getEntity(permanent)?.get<TappedComponent>() != null) shouldBe true
            }

            test("mode 2 can't target a non-Zombie creature card in your graveyard") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Deadly Plot")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findCardsInGraveyard(1, "Grizzly Bears").single()
                val plot = game.findCardsInHand(1, "Deadly Plot").single()
                val target = listOf(ChosenTarget.Card(bears, game.player1Id, Zone.GRAVEYARD))

                val result = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = plot,
                        targets = target,
                        chosenModes = listOf(1),
                        modeTargetsOrdered = listOf(target),
                    )
                )
                (result.error != null) shouldBe true
                game.isOnBattlefield("Grizzly Bears") shouldBe false
            }
        }
    }
}
