package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class SureshotSowerScenarioTest : ScenarioTestBase() {
    init {
        test("discard activation destroys a flyer and pays the discard before resolution") {
            val game = scenario().withPlayers().withCardInHand(1, "Sureshot Sower")
                .withCardOnBattlefield(2, "Air Elemental").withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 4)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val sower = game.findCardsInHand(1, "Sureshot Sower").single()
            val ability = cardRegistry.getCard("Sureshot Sower")!!.script.activatedAbilities.single().id
            game.execute(ActivateAbility(game.player1Id, sower, ability,
                targets = listOf(ChosenTarget.Permanent(game.findPermanent("Grizzly Bears")!!)))).error.isNullOrEmpty() shouldBe false
            game.isInHand(1, "Sureshot Sower") shouldBe true
            game.execute(ActivateAbility(game.player1Id, sower, ability,
                targets = listOf(ChosenTarget.Permanent(game.findPermanent("Air Elemental")!!)))).error shouldBe null
            game.isInGraveyard(1, "Sureshot Sower") shouldBe true
            game.resolveStack()
            game.isInGraveyard(2, "Air Elemental") shouldBe true
            game.isOnBattlefield("Grizzly Bears") shouldBe true
        }
    }
}
