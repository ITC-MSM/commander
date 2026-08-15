package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Angel of Finality (C13 #4, reprinted FDN #136) —
 * {3}{W} Creature — Angel, 3/4, Flying.
 *
 * "When this creature enters, exile target player's graveyard." Pins the whole-graveyard
 * gather-then-move over the targeted player's graveyard, plus the empty-graveyard edge case.
 */
class AngelOfFinalityScenarioTest : ScenarioTestBase() {

    init {
        context("Angel of Finality") {

            test("ETB exiles the targeted player's graveyard") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardInHand(1, "Angel of Finality")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("precondition: opponent has two cards in graveyard") {
                    game.graveyardSize(2) shouldBe 2
                }

                val cast = game.castSpell(1, "Angel of Finality")
                withClue("Angel should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision for the ETB; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(game.player2Id))))
                game.resolveStack()

                withClue("the targeted player's graveyard is exiled (emptied)") {
                    game.graveyardSize(2) shouldBe 0
                }
                withClue("the Angel resolved onto the battlefield") {
                    (game.findPermanent("Angel of Finality") != null) shouldBe true
                }
            }

            test("ETB resolves cleanly against a player with an empty graveyard") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardInHand(1, "Angel of Finality")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Angel of Finality")
                withClue("Angel should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision for the ETB; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(game.player2Id))))
                game.resolveStack()

                withClue("no crash; opponent's graveyard stays empty") {
                    game.graveyardSize(2) shouldBe 0
                }
            }
        }
    }
}
