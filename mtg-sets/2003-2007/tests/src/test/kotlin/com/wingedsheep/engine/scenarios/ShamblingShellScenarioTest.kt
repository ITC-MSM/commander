package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class ShamblingShellScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Shambling Shell")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Shambling Shell"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Shambling Shell") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 3
            game.state.getLibrary(game.player1Id).size shouldBe 1
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        test("sacrificing Shell puts a permanent counter on the targeted creature") {
            val game = base().withCardOnBattlefield(1, "Shambling Shell")
                .withCardOnBattlefield(1, "Grizzly Bears").build()
            val shell = game.findPermanent("Shambling Shell")!!
            val bear = game.findPermanent("Grizzly Bears")!!
            val ability = cardRegistry.getCard("Shambling Shell")!!.activatedAbilities.single()
            game.execute(ActivateAbility(game.player1Id, shell, ability.id,
                targets = listOf(ChosenTarget.Permanent(bear)))).error shouldBe null
            game.isInGraveyard(1, "Shambling Shell") shouldBe true
            game.resolveStack()
            game.state.projectedState.getPower(bear) shouldBe 3
            game.state.projectedState.getToughness(bear) shouldBe 3
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.projectedState.getPower(bear) shouldBe 3
        }
    }
}
