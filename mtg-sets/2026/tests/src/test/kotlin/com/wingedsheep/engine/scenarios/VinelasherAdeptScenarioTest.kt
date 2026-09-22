package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class VinelasherAdeptScenarioTest : ScenarioTestBase() {
    init {
        test("enters trigger can put all three counters on an opposing creature") {
            val game = scenario().withPlayers().withCardInHand(1, "Vinelasher Adept")
                .withCardOnBattlefield(2, "Grizzly Bears").withLandsOnBattlefield(1, "Forest", 6)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Vinelasher Adept").error shouldBe null
            game.resolveStack()
            game.selectTargets(listOf(bears)).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(bears) shouldBe 5
            game.state.projectedState.getToughness(bears) shouldBe 5
        }
    }
}
