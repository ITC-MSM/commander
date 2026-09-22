package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.sdk.core.Keyword

class TethermagesAdvantageScenarioTest : ScenarioTestBase() {
    init {
        test("untaps and grants temporary stats and reach to either player's creature") {
            for (controller in listOf(1, 2)) {
                val game = scenario().withPlayers()
                    .withCardInHand(1, "Tethermage's Advantage")
                    .withCardOnBattlefield(controller, "Grizzly Bears", tapped = true)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInLibrary(1, "Forest").withCardInLibrary(2, "Forest")
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                val bear = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Tethermage's Advantage", bear).error shouldBe null
                game.resolveStack()
                game.state.getEntity(bear)!!.has<TappedComponent>() shouldBe false
                game.state.projectedState.getPower(bear) shouldBe 4
                game.state.projectedState.getToughness(bear) shouldBe 4
                game.state.projectedState.hasKeyword(bear, Keyword.REACH) shouldBe true
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                game.state.projectedState.getPower(bear) shouldBe 2
                game.state.projectedState.getToughness(bear) shouldBe 2
                game.state.projectedState.hasKeyword(bear, Keyword.REACH) shouldBe false
            }
        }
    }
}
