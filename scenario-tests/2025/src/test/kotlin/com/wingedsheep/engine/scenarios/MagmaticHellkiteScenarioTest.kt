package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Magmatic Hellkite (TDM #111).
 *
 * "Flying. When this creature enters, destroy target nonbasic land an opponent controls.
 *  Its controller searches their library for a basic land card, puts it onto the battlefield
 *  tapped with a stun counter on it, then shuffles."
 *
 * Verifies the opponent-scoped compensating-ramp pipeline:
 *  - the targeted nonbasic land is destroyed,
 *  - the land's controller (the opponent, not the Hellkite's controller) finds a basic and
 *    puts it onto *their* battlefield tapped with a stun counter.
 */
class MagmaticHellkiteScenarioTest : ScenarioTestBase() {

    init {
        context("Magmatic Hellkite ETB") {

            test("destroys opponent's nonbasic land and ramps them a tapped, stunned basic") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Magmatic Hellkite")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    // Opponent controls a nonbasic land + has a basic in their library to ramp.
                    .withCardOnBattlefield(2, "Bloodfell Caves")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val me = game.player1Id
                val opponent = game.player2Id
                val nonbasic = game.findPermanent("Bloodfell Caves")!!

                game.castSpell(1, "Magmatic Hellkite").error shouldBe null
                game.resolveStack() // Hellkite enters → ETB trigger on stack, asks for a target land

                withClue("ETB trigger should prompt to target the opponent's nonbasic land") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(nonbasic)).error shouldBe null
                game.resolveStack() // trigger resolves: destroy + opponent search prompt

                // The opponent now selects a basic from their library.
                if (game.hasPendingDecision()) {
                    val decision = game.getPendingDecision()
                    val options = (decision as? com.wingedsheep.engine.core.SelectCardsDecision)?.options
                        ?: emptyList()
                    if (options.isNotEmpty()) {
                        game.selectCards(listOf(options.first()))
                    } else {
                        game.skipSelection()
                    }
                    game.resolveStack()
                }

                withClue("The targeted nonbasic land should be destroyed") {
                    game.findPermanent("Bloodfell Caves") shouldBe null
                    game.findCardsInGraveyard(2, "Bloodfell Caves").size shouldBe 1
                }

                val forestId = game.findPermanents("Forest").singleOrNull()
                withClue("The opponent should have searched up a Forest onto the battlefield") {
                    (forestId != null) shouldBe true
                }
                withClue("The Forest enters under the opponent's control, not the caster's") {
                    game.state.getEntity(forestId!!)?.get<ControllerComponent>()?.playerId shouldBe opponent
                }
                withClue("The Forest enters tapped") {
                    (game.state.getEntity(forestId!!)?.get<TappedComponent>() != null) shouldBe true
                }
                withClue("The Forest enters with a stun counter") {
                    game.state.getEntity(forestId!!)?.get<CountersComponent>()
                        ?.getCount(CounterType.STUN) shouldBe 1
                }
                withClue("The Forest left the opponent's library") {
                    game.findCardsInLibrary(2, "Forest").size shouldBe 0
                }
                // The Hellkite's controller gained nothing on their own battlefield.
                withClue("No Forest under the caster's control") {
                    game.findPermanents("Forest").none {
                        game.state.getEntity(it)?.get<ControllerComponent>()?.playerId == me
                    } shouldBe true
                }
            }

            test("destroys the land even when the opponent has no basic to find") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Magmatic Hellkite")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(2, "Bloodfell Caves")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val nonbasic = game.findPermanent("Bloodfell Caves")!!

                game.castSpell(1, "Magmatic Hellkite").error shouldBe null
                game.resolveStack()
                game.selectTargets(listOf(nonbasic)).error shouldBe null
                game.resolveStack()
                if (game.hasPendingDecision()) {
                    game.skipSelection()
                    game.resolveStack()
                }

                withClue("The land is destroyed regardless of whether a basic was found") {
                    game.findPermanent("Bloodfell Caves") shouldBe null
                }
                withClue("No basic appeared since the library had none") {
                    game.findPermanents("Forest").isEmpty() shouldBe true
                }
            }
        }
    }
}
