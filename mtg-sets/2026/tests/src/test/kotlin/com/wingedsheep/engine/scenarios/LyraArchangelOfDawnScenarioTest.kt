package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class LyraArchangelOfDawnScenarioTest : ScenarioTestBase() {
    init {
        test("gaining life puts a +1/+1 counter on each Angel you control, including Lyra") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Lyra, Archangel of Dawn")
                .withCardOnBattlefield(1, "Serra Angel")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Serra Angel")
                .withCardInHand(1, "Sacred Nectar")
                .withLandsOnBattlefield(1, "Plains", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Sacred Nectar").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 24
            val projected = game.state.projectedState
            projected.getPower(game.findPermanent("Lyra, Archangel of Dawn")!!) shouldBe 4
            val angels = game.findPermanents("Serra Angel")
            val mine = angels.single { game.state.getBattlefield(game.player1Id).contains(it) }
            val theirs = angels.single { it != mine }
            projected.getPower(mine) shouldBe 5
            projected.getPower(theirs) shouldBe 4
            projected.getPower(game.findPermanent("Grizzly Bears")!!) shouldBe 2
        }
    }
}
