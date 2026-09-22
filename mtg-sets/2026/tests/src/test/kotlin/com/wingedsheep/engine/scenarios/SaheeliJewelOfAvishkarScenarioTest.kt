package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class SaheeliJewelOfAvishkarScenarioTest : ScenarioTestBase() {
    init {
        test("casting a noncreature spell creates a flying Thopter artifact that has haste") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Saheeli, Jewel of Avishkar")
                .withCardInHand(1, "Shock")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
            game.resolveStack()
            game.getLifeTotal(2) shouldBe 18
            val thopter = game.findPermanent("Thopter Token")!!
            val projected = game.state.projectedState
            projected.isCreature(thopter) shouldBe true
            projected.hasType(thopter, "ARTIFACT") shouldBe true
            projected.getColors(thopter) shouldBe emptySet()
            projected.hasKeyword(thopter, Keyword.FLYING) shouldBe true
            projected.hasKeyword(thopter, Keyword.HASTE) shouldBe true
        }

        test("casting a creature spell does not create a Thopter") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Saheeli, Jewel of Avishkar")
                .withCardInHand(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Grizzly Bears").error shouldBe null
            game.resolveStack()
            game.findPermanent("Thopter Token") shouldBe null
            game.state.projectedState.hasKeyword(game.findPermanent("Grizzly Bears")!!, Keyword.HASTE) shouldBe false
        }
    }
}
