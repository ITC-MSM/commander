package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

class StitchedDrakeScenarioTest : ScenarioTestBase() {
    init {
        test("casting exiles a creature card from the graveyard as an additional cost") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Stitched Drake")
                .withCardInGraveyard(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val drake = game.findCardsInHand(1, "Stitched Drake").single()
            val bears = game.findCardsInGraveyard(1, "Grizzly Bears").single()

            val cast = game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = drake,
                    targets = emptyList(),
                    additionalCostPayment = AdditionalCostPayment(exiledCards = listOf(bears)),
                ),
            )
            withClue("casting should succeed: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()

            game.isInGraveyard(1, "Grizzly Bears") shouldBe false
            game.isInExile(1, "Grizzly Bears") shouldBe true
            game.isOnBattlefield("Stitched Drake") shouldBe true
        }
    }
}
