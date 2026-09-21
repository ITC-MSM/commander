package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class ArtifistAcumenScenarioTest : ScenarioTestBase() {
    init {
        test("grants first strike only to your current creatures, draws, and expires") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Artifist Acumen")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Island").withCardInLibrary(2, "Island").withCardInLibrary(2, "Island")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bear = game.findPermanent("Grizzly Bears")!!
            val giant = game.findPermanent("Hill Giant")!!
            game.castSpell(1, "Artifist Acumen").error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Forest") shouldBe true
            game.state.projectedState.hasKeyword(bear, Keyword.FIRST_STRIKE) shouldBe true
            game.state.projectedState.hasKeyword(giant, Keyword.FIRST_STRIKE) shouldBe false
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.state.projectedState.hasKeyword(bear, Keyword.FIRST_STRIKE) shouldBe false
        }

        test("draws even when you control no creatures") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Artifist Acumen")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Forest")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Artifist Acumen").error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Forest") shouldBe true
        }
    }
}
