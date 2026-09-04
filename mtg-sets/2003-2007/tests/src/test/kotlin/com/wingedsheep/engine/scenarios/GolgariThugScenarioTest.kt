package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class GolgariThugScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Golgari Thug")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Golgari Thug"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Golgari Thug") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 4
            game.state.getLibrary(game.player1Id).size shouldBe 1
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        test("dies trigger can target the Thug itself and put it on top") {
            val game = base().withCardOnBattlefield(1, "Golgari Thug")
                .withCardInHand(1, "Shock").withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Forest").build()
            game.castSpell(1, "Shock", game.findPermanent("Golgari Thug")!!).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision is ChooseTargetsDecision) shouldBe true
            val thug = game.findCardsInGraveyard(1, "Golgari Thug").single()
            game.selectTargets(listOf(thug)).error shouldBe null
            game.resolveStack()
            game.state.getLibrary(game.player1Id).first() shouldBe thug
            game.isInGraveyard(1, "Golgari Thug") shouldBe false
        }

        test("dies trigger returns another creature from your graveyard") {
            val game = base().withCardOnBattlefield(1, "Golgari Thug")
                .withCardInGraveyard(1, "Grizzly Bears")
                .withCardInHand(1, "Shock").withLandsOnBattlefield(1, "Mountain", 1).build()
            val bear = game.findCardsInGraveyard(1, "Grizzly Bears").single()
            game.castSpell(1, "Shock", game.findPermanent("Golgari Thug")!!).error shouldBe null
            game.resolveStack()
            game.selectTargets(listOf(bear)).error shouldBe null
            game.resolveStack()
            game.state.getLibrary(game.player1Id).first() shouldBe bear
            game.isInGraveyard(1, "Golgari Thug") shouldBe true
        }
    }
}
