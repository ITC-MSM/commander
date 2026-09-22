package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class WreckingGeckoScenarioTest : ScenarioTestBase() {
    init {
        test("activation grants the full pump and trample") {
            val game = scenario().withPlayers().withCardOnBattlefield(1, "Wrecking Gecko")
                .withLandsOnBattlefield(1, "Forest", 8)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val gecko = game.findPermanent("Wrecking Gecko")!!
            val ability = cardRegistry.getCard("Wrecking Gecko")!!.script.activatedAbilities.single().id
            game.execute(ActivateAbility(game.player1Id, gecko, ability)).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(gecko) shouldBe 9
            game.state.projectedState.getToughness(gecko) shouldBe 9
            game.state.projectedState.hasKeyword(gecko, Keyword.TRAMPLE) shouldBe true
        }
    }
}
