package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class PiaDeterminedRebuilderScenarioTest : ScenarioTestBase() {
    init {
        test("enters creates the correct Thopter token") {
            val game = scenario().withPlayers().withCardInHand(1, "Pia, Determined Rebuilder")
                .withLandsOnBattlefield(1, "Mountain", 3)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Pia, Determined Rebuilder").error shouldBe null
            game.resolveStack()
            val token = game.findPermanent("Thopter Token")!!
            game.state.projectedState.getPower(token) shouldBe 1
            game.state.projectedState.getToughness(token) shouldBe 1
            game.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true
        }
        test("pump counts your artifacts only and can target an opposing creature") {
            val game = scenario().withPlayers().withCardOnBattlefield(1, "Pia, Determined Rebuilder")
                .withCardOnBattlefield(1, "Sol Ring").withCardOnBattlefield(1, "Ornithopter")
                .withCardOnBattlefield(2, "Sol Ring").withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Mountain", 6)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val pia = game.findPermanent("Pia, Determined Rebuilder")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val ability = cardRegistry.getCard("Pia, Determined Rebuilder")!!.script.activatedAbilities.single().id
            game.execute(ActivateAbility(game.player1Id, pia, ability, targets = listOf(ChosenTarget.Permanent(bears)))).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(bears) shouldBe 4
            game.state.projectedState.getToughness(bears) shouldBe 2
        }
    }
}
