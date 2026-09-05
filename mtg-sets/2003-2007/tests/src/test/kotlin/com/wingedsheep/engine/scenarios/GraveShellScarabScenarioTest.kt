package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class GraveShellScarabScenarioTest : ScenarioTestBase() {
    init {
        fun base() = scenario().withPlayers("P1", "P2")
            .withActivePlayer(1).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("dredge returns this card and mills its printed number instead of one draw") {
            val game = base().withCardInGraveyard(1, "Grave-Shell Scarab")
                .withCardInHand(1, "Inspiration")
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Forest").build()
            game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
            game.resolveStack()
            (game.state.pendingDecision as YesNoDecision).context.sourceName shouldBe "Grave-Shell Scarab"
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Grave-Shell Scarab") shouldBe true
            game.findCardsInGraveyard(1, "Forest").size shouldBe 1
            game.state.getLibrary(game.player1Id).size shouldBe 3
            game.state.getHand(game.player1Id).size shouldBe 2
        }

        test("sacrificing Scarab can dredge itself back with its own draw ability") {
            val game = base().withCardOnBattlefield(1, "Grave-Shell Scarab")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInLibrary(1, "Swamp").build()
            val source = game.findPermanent("Grave-Shell Scarab")!!
            val ability = cardRegistry.getCard("Grave-Shell Scarab")!!.activatedAbilities.single()
            game.execute(ActivateAbility(game.player1Id, source, ability.id)).error shouldBe null
            game.isInGraveyard(1, "Grave-Shell Scarab") shouldBe true
            game.resolveStack()
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()
            game.isInHand(1, "Grave-Shell Scarab") shouldBe true
            game.isInGraveyard(1, "Swamp") shouldBe true
            game.state.getHand(game.player1Id).size shouldBe 1
        }
    }
}
