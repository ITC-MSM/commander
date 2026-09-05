package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class GreaterMossdogScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Greater Mossdog")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Greater Mossdog"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Greater Mossdog") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 3
            game.state.getLibrary(game.player1Id).size shouldBe 1
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        test("casting Mossdog produces a three-three creature") {
            val game = base().withCardInHand(1, "Greater Mossdog")
                .withLandsOnBattlefield(1, "Forest", 4).build()
            game.castSpell(1, "Greater Mossdog").error shouldBe null
            game.resolveStack()
            val dog = game.findPermanent("Greater Mossdog")!!
            game.state.projectedState.getPower(dog) shouldBe 3
            game.state.projectedState.getToughness(dog) shouldBe 3
        }
    }
}
