package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.WarpExiledComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Blade of the Swarm (EOE) — {3}{B} Creature — Insect Assassin, 3/1.
 *
 * "When this creature enters, choose one —
 *  • Put two +1/+1 counters on this creature.
 *  • Put target exiled card with warp on the bottom of its owner's library."
 *
 * Exercises the modal ETB trigger, the new exile-zone branch of
 * [com.wingedsheep.engine.legalactions.utils.TargetEnumerationUtils.findValidObjectTargets],
 * and the cross-zone move from exile to library bottom.
 */
class BladeOfTheSwarmScenarioTest : ScenarioTestBase() {

    init {
        context("Blade of the Swarm's ETB modal trigger") {

            test("mode 2 puts a warp-exiled card on the bottom of its owner's library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Blade of the Swarm")
                    .withCardInExile(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Stamp the WarpExiledComponent on the exiled Grizzly Bears so the warp-exile
                // predicate matches it at targeting time.
                val exileZone = game.state.getZone(ZoneKey(game.player1Id, Zone.EXILE))
                val warpBears = exileZone.first { entityId ->
                    game.state.getEntity(entityId)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                game.state = game.state.updateEntity(warpBears) { container ->
                    container.with(WarpExiledComponent(controllerId = game.player1Id))
                }

                val cast = game.castSpell(1, "Blade of the Swarm")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // The ETB trigger is on the stack; resolving it surfaces a ChooseModeDecision.
                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision for the ETB trigger; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 1))

                // Mode 1 (zero-indexed) is the warp-exile branch — engine then asks for the target.
                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision after mode pick; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(warpBears))))
                game.resolveStack()

                val libraryAfter = game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY))
                val exileAfter = game.state.getZone(ZoneKey(game.player1Id, Zone.EXILE))
                withClue("Grizzly Bears should have moved out of exile") {
                    exileAfter.contains(warpBears) shouldBe false
                }
                withClue("Grizzly Bears should be on the bottom of player 1's library") {
                    libraryAfter.last() shouldBe warpBears
                }
            }

            test("opponent's warp-exiled card is also a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Blade of the Swarm")
                    .withCardInExile(2, "Hill Giant")
                    .withLandsOnBattlefield(1, "Swamp", 4)
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Stamp opponent's Hill Giant as warp-exiled (warp markers point at the
                // *original* controller — the opponent — so they still own it in exile).
                val exileZone = game.state.getZone(ZoneKey(game.player2Id, Zone.EXILE))
                val warpGiant = exileZone.first { entityId ->
                    game.state.getEntity(entityId)?.get<CardComponent>()?.name == "Hill Giant"
                }
                game.state = game.state.updateEntity(warpGiant) { container ->
                    container.with(WarpExiledComponent(controllerId = game.player2Id))
                }

                game.castSpell(1, "Blade of the Swarm").error shouldBe null
                game.resolveStack()

                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 1))

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision after mode pick")
                withClue("Opponent's warp-exiled Hill Giant should be a legal target") {
                    targetDecision.legalTargets[0]?.contains(warpGiant) shouldBe true
                }

                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(warpGiant))))
                game.resolveStack()

                val opponentLibrary = game.state.getZone(ZoneKey(game.player2Id, Zone.LIBRARY))
                withClue("Hill Giant should be on the bottom of opponent's library (it's its owner)") {
                    opponentLibrary.last() shouldBe warpGiant
                }
            }
        }
    }
}
