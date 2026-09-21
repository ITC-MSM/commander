package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class ChandrasEmberlingScenarioTest : ScenarioTestBase() {
    init {
        test("has haste and grows for a noncreature spell but not a creature spell") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Chandra's Emberling")
                .withCardInHand(1, "Artifist Acumen")
                .withCardInHand(1, "Raging Goblin")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withCardInLibrary(1, "Mountain").withCardInLibrary(1, "Mountain").withCardInLibrary(1, "Mountain")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val emberling = game.findPermanent("Chandra's Emberling")!!
            game.state.projectedState.hasKeyword(emberling, Keyword.HASTE) shouldBe true
            game.castSpell(1, "Artifist Acumen").error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(emberling) shouldBe 3
            game.state.projectedState.getToughness(emberling) shouldBe 3
            game.castSpell(1, "Raging Goblin").error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(emberling) shouldBe 3
        }
    }
}
