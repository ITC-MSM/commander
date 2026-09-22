package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class FrostbitePyromentalScenarioTest : ScenarioTestBase() {
    init {
        listOf(1, 2).forEach { activePlayer ->
            test("sacrifices at player $activePlayer's end step") {
                val game = scenario().withPlayers()
                    .withCardOnBattlefield(1, "Frostbite Pyromental")
                    .withActivePlayer(activePlayer)
                    .inPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN).build()
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()
                game.isInGraveyard(1, "Frostbite Pyromental") shouldBe true
            }
        }

        test("combat damage to a player draws two cards") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Frostbite Pyromental")
                .withCardInLibrary(1, "Island").withCardInLibrary(1, "Island")
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS).build()
            game.declareAttackers(mapOf("Frostbite Pyromental" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareNoBlockers()
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
            game.findCardsInHand(1, "Island").size shouldBe 2
            game.getLifeTotal(2) shouldBe 16
        }
    }
}
