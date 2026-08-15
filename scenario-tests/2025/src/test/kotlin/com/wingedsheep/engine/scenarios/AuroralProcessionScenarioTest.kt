package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.engine.state.components.identity.CardComponent
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Auroral Procession (TDM #169).
 *
 * "{G}{U} Instant — Return target card from your graveyard to your hand."
 *
 * Verifies the regrowth-style return from your own graveyard. Non-creature cards are
 * valid targets (the filter is "any card you own in your graveyard").
 */
class AuroralProcessionScenarioTest : ScenarioTestBase() {

    init {
        context("Auroral Procession") {

            test("returns a target card from your graveyard to your hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Auroral Procession")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpellTargetingGraveyardCard(
                    playerNumber = 1,
                    spellName = "Auroral Procession",
                    graveyardOwnerNumber = 1,
                    targetCardName = "Grizzly Bears"
                )
                withClue("Casting Auroral Procession should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Grizzly Bears should no longer be in the graveyard") {
                    game.findCardsInGraveyard(1, "Grizzly Bears").size shouldBe 0
                }
                withClue("Grizzly Bears should be back in hand") {
                    game.state.getHand(game.player1Id).count { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                    } shouldBe 1
                }
            }
        }
    }
}
