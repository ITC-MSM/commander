package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class MoldervineCloakScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Moldervine Cloak")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Moldervine Cloak"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Moldervine Cloak") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 2
            game.state.getLibrary(game.player1Id).size shouldBe 2
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        test("the aura gives the enchanted creature plus three plus three") {
            val game = base().withCardInHand(1, "Moldervine Cloak")
                .withLandsOnBattlefield(1, "Forest", 3)
                .withCardOnBattlefield(1, "Grizzly Bears").build()
            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Moldervine Cloak", bear).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(bear) shouldBe 5
            game.state.projectedState.getToughness(bear) shouldBe 5
        }
    }
}
