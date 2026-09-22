package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class HeartstringPullerScenarioTest : ScenarioTestBase() {
    init {
        test("enters creates the correct Cadet token") {
            val game = scenario().withPlayers().withCardInHand(1, "Heartstring Puller")
                .withLandsOnBattlefield(1, "Mountain", 4)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Heartstring Puller").error shouldBe null
            game.resolveStack()
            val token = game.findPermanent("Cadet")!!
            game.state.projectedState.getPower(token) shouldBe 2
            game.state.projectedState.getToughness(token) shouldBe 2
        }
    }
}
