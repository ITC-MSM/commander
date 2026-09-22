package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class ChargeTheSanctumScenarioTest : ScenarioTestBase() {
    private fun board() = scenario().withPlayers()
        .withCardInHand(1, "Charge the Sanctum")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withCardOnBattlefield(1, "Hill Giant")
        .withCardOnBattlefield(2, "Goblin Piker")
        .withLandsOnBattlefield(1, "Mountain", 3)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

    init {
        test("creatures you control get +2/+0") {
            val game = board()
            game.castSpellWithMode(1, "Charge the Sanctum", 0).error shouldBe null
            game.resolveStack()
            val projected = game.state.projectedState
            projected.getPower(game.findPermanent("Grizzly Bears")!!) shouldBe 4
            projected.getPower(game.findPermanent("Hill Giant")!!) shouldBe 5
            projected.getPower(game.findPermanent("Goblin Piker")!!) shouldBe 2
        }

        test("target creature gets +2/+0, first strike, and a +1/+1 counter") {
            val game = board()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpellWithMode(1, "Charge the Sanctum", 1, bears).error shouldBe null
            game.resolveStack()
            val projected = game.state.projectedState
            projected.getPower(bears) shouldBe 5
            projected.getToughness(bears) shouldBe 3
            projected.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe true
        }
    }
}
