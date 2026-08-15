package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Price of Freedom (TLA #149).
 *
 * "{1}{R} Sorcery — Lesson. Destroy target artifact or land an opponent controls. Its controller may
 *  search their library for a basic land card, put it onto the battlefield tapped, then shuffle.
 *  Draw a card."
 *
 * Reproduces the playtest bug where the destroyed land's controller was never allowed to choose a
 * basic land: the `SelectFromCollectionEffect` defaulted to [Chooser.Controller] (the caster), so the
 * *opponent* silently made the fetch selection. The fix pins it to [Chooser.ControllerOfTarget]. These
 * tests assert both the outcome (the victim ramps a tapped basic; the caster draws) and — critically —
 * that the selection decision belongs to the victim, not the caster.
 */
class PriceOfFreedomScenarioTest : ScenarioTestBase() {

    init {
        context("Price of Freedom") {

            test("destroys the target land; its controller chooses and fetches a tapped basic") {
                val game = scenario()
                    .withPlayers("Caster", "Victim")
                    .withCardInHand(1, "Price of Freedom")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLandsOnBattlefield(2, "Swamp", 1)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val victimSwamp = game.state.getBattlefield().first { id ->
                    val e = game.state.getEntity(id)
                    e?.get<CardComponent>()?.name == "Swamp" &&
                        e.get<ControllerComponent>()?.playerId == game.player2Id
                }

                game.castSpell(1, "Price of Freedom", victimSwamp)
                game.resolveStack()

                // Its controller (player 2) may search — accept.
                game.hasPendingDecision() shouldBe true
                game.getPendingDecision()?.playerId shouldBe game.player2Id
                game.answerYesNo(true)
                game.resolveStack()

                // The basic-land selection must be made by the destroyed land's controller
                // (player 2), NOT the caster (player 1). This is the regression guard: a missing
                // Chooser.ControllerOfTarget routed the selection to the caster.
                withClue("Player 2 (the land's controller) should choose the basic land") {
                    game.getPendingDecision()?.playerId shouldBe game.player2Id
                }

                // Player 2 fetches the Forest.
                if (game.hasPendingDecision()) {
                    val forest = game.state.getLibrary(game.player2Id).first { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                    }
                    game.selectCards(listOf(forest))
                    game.resolveStack()
                }

                withClue("The targeted Swamp should be destroyed") {
                    game.state.getBattlefield().none { id ->
                        val e = game.state.getEntity(id)
                        e?.get<CardComponent>()?.name == "Swamp" &&
                            e.get<ControllerComponent>()?.playerId == game.player2Id
                    } shouldBe true
                }
                val forestOnBf = game.state.getBattlefield().firstOrNull { id ->
                    val e = game.state.getEntity(id)
                    e?.get<CardComponent>()?.name == "Forest" &&
                        e.get<ControllerComponent>()?.playerId == game.player2Id
                }
                withClue("Player 2 should have fetched a Forest onto the battlefield") {
                    (forestOnBf != null) shouldBe true
                }
                withClue("The fetched Forest should be tapped") {
                    game.state.getEntity(forestOnBf!!)?.get<TappedComponent>() shouldBe TappedComponent
                }
            }

            test("the land's controller may decline the search; caster still draws") {
                val game = scenario()
                    .withPlayers("Caster", "Victim")
                    .withCardInHand(1, "Price of Freedom")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLandsOnBattlefield(2, "Swamp", 1)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val casterHandBefore = game.handSize(1)

                val victimSwamp = game.state.getBattlefield().first { id ->
                    val e = game.state.getEntity(id)
                    e?.get<CardComponent>()?.name == "Swamp" &&
                        e.get<ControllerComponent>()?.playerId == game.player2Id
                }

                game.castSpell(1, "Price of Freedom", victimSwamp)
                game.resolveStack()

                game.hasPendingDecision() shouldBe true
                game.getPendingDecision()?.playerId shouldBe game.player2Id
                game.answerYesNo(false)
                game.resolveStack()

                withClue("The targeted Swamp should still be destroyed") {
                    game.state.getBattlefield().none { id ->
                        val e = game.state.getEntity(id)
                        e?.get<CardComponent>()?.name == "Swamp" &&
                            e.get<ControllerComponent>()?.playerId == game.player2Id
                    } shouldBe true
                }
                withClue("No Forest should have entered the battlefield") {
                    game.state.getBattlefield().none { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                    } shouldBe true
                }
                // "Draw a card" — the caster (player 1) draws, casting Price of Freedom (−1) then
                // drawing (+1) nets back to the starting hand size.
                withClue("The caster should have drawn a card") {
                    game.handSize(1) shouldBe casterHandBefore
                }
            }
        }
    }
}
