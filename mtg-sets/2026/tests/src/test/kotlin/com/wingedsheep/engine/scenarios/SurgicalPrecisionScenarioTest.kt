package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class SurgicalPrecisionScenarioTest : ScenarioTestBase() {
    init {
        test("removal mode destroys a large creature and gains one life") {
            val game = scenario().withPlayers().withCardInHand(1, "Surgical Precision")
                .withCardOnBattlefield(2, "Giant Spider").withLandsOnBattlefield(1, "Plains", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellWithMode(1, "Surgical Precision", 0, game.findPermanent("Giant Spider")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Giant Spider") shouldBe true
            game.getLifeTotal(1) shouldBe 21
        }
        test("draw mode needs no target and gains two life") {
            val game = scenario().withPlayers().withCardInHand(1, "Surgical Precision")
                .withCardInLibrary(1, "Forest").withLandsOnBattlefield(1, "Plains", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellWithMode(1, "Surgical Precision", 1).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Forest") shouldBe true
            game.getLifeTotal(1) shouldBe 22
        }
    }
}
