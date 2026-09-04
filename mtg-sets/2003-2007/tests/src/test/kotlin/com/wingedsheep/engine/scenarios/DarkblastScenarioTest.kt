package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class DarkblastScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Darkblast")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Darkblast"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Darkblast") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 3
            game.state.getLibrary(game.player1Id).size shouldBe 1
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        test("the spell shrinks its target until end of turn") {
            val game = base().withCardInHand(1, "Darkblast")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withCardOnBattlefield(2, "Grizzly Bears").build()
            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Darkblast", bear).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(bear) shouldBe 1
            game.state.projectedState.getToughness(bear) shouldBe 1
            game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            game.state.projectedState.getPower(bear) shouldBe 2
            game.state.projectedState.getToughness(bear) shouldBe 2
        }
    }
}
