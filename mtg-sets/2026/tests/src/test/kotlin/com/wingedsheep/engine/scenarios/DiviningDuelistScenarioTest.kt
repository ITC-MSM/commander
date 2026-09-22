package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class DiviningDuelistScenarioTest : ScenarioTestBase() {
    init {
        for (mode in listOf(0, 1, 2)) {
            test("enters modal trigger resolves mode $mode") {
                val game = scenario().withPlayers().withCardInHand(1, "Divining Duelist")
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = mode == 1)
                    .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Island")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Divining Duelist").error shouldBe null
                game.resolveStack()
                val decision = game.state.pendingDecision as ChooseOptionDecision
                game.submitDecision(OptionChosenResponse(decision.id, mode)).error shouldBe null
                game.resolveStack()
                if (mode < 2) {
                    game.selectTargets(listOf(bears)).error shouldBe null
                    game.resolveStack()
                    game.state.getEntity(bears)!!.has<TappedComponent>() shouldBe (mode == 0)
                } else {
                    if (game.hasPendingDecision()) game.selectCards(game.state.getHand(game.player1Id).take(1)).error shouldBe null
                    game.resolveStack()
                    game.handSize(1) shouldBe 0
                    game.graveyardSize(1) shouldBe 1
                    game.librarySize(1) shouldBe 1
                }
                game.hasPendingDecision() shouldBe false
            }
        }
    }
}
