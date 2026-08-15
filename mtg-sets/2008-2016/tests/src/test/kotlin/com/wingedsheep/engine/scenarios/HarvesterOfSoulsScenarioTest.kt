package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class HarvesterOfSoulsScenarioTest : ScenarioTestBase() {
    init {
        test("when another nontoken creature dies, you may draw a card") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Harvester of Souls")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(1, "Shock")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val shock = game.findCardsInHand(1, "Shock").single()
            val handBefore = game.state.getZone(game.player1Id, Zone.HAND).size

            val cast = game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = shock,
                    targets = listOf(entityIdToChosenTarget(game.state, bears)),
                ),
            )
            withClue("Shock cast: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()

            game.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Harvester of Souls") shouldBe true
            game.isOnBattlefield("Grizzly Bears") shouldBe false
            // Shock left hand (-1), then may-draw yes (+1) → back to handBefore.
            withClue("drew a card after accepting may") {
                game.state.getZone(game.player1Id, Zone.HAND).size shouldBe handBefore
            }
        }
    }
}
