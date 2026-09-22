package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class KioraOfFireAndAshesScenarioTest : ScenarioTestBase() {
    init {
        test("enters creates the correct Dragon token") {
            val game = scenario().withPlayers().withCardInHand(1, "Kiora of Fire and Ashes")
                .withLandsOnBattlefield(1, "Mountain", 6)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Kiora of Fire and Ashes").error shouldBe null
            game.resolveStack()
            val token = game.findPermanent("Dragon Token")!!
            game.state.projectedState.getPower(token) shouldBe 5
            game.state.projectedState.getToughness(token) shouldBe 5
            game.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true
        }
        test("activation creates another Dragon") {
            val game = scenario().withPlayers().withCardOnBattlefield(1, "Kiora of Fire and Ashes")
                .withLandsOnBattlefield(1, "Mountain", 8)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val kiora = game.findPermanent("Kiora of Fire and Ashes")!!
            val ability = cardRegistry.getCard("Kiora of Fire and Ashes")!!.script.activatedAbilities.single().id
            game.execute(ActivateAbility(game.player1Id, kiora, ability)).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(game.findPermanent("Dragon Token")!!) shouldBe 5
        }
    }
}
