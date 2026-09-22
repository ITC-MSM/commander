package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class PerfectedTheoryScenarioTest : ScenarioTestBase() {
    init {
        listOf(1 to 1, 4 to 5).forEachIndexed { mode, (power, toughness) ->
            test("mode $mode sets base stats, preserves bonuses, and expires at cleanup") {
                val game = scenario().withPlayers()
                    .withCardInHand(1, "Perfected Theory")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Glorious Anthem")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withCardInLibrary(1, "Island").withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Forest").withCardInLibrary(2, "Forest")
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                val bear = game.findPermanent("Grizzly Bears")!!
                game.castSpellWithMode(1, "Perfected Theory", modeIndex = mode, targetId = bear).error shouldBe null
                game.resolveStack()
                game.state.projectedState.getPower(bear) shouldBe power + 1
                game.state.projectedState.getToughness(bear) shouldBe toughness + 1
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.state.projectedState.getPower(bear) shouldBe 3
                game.state.projectedState.getToughness(bear) shouldBe 3
            }
        }
    }
}
