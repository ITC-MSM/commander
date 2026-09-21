package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class CastAwayDoubtScenarioTest : ScenarioTestBase() {
    init {
        test("draws two cards and damages both players") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Cast Away Doubt")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withCardInLibrary(1, "Swamp").withCardInLibrary(1, "Swamp").withCardInLibrary(1, "Swamp")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Cast Away Doubt").error shouldBe null
            game.resolveStack()
            game.findCardsInHand(1, "Swamp").size shouldBe 2
            game.getLifeTotal(1) shouldBe 18
            game.getLifeTotal(2) shouldBe 18
        }
    }
}
