package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class ProphesiedEndScenarioTest : ScenarioTestBase() {
    init {
        test("a non-attacking creature is destroyed and its controller draws") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Prophesied End")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Forest")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val opponentHand = game.handSize(2)
            game.castSpell(1, "Prophesied End", bears).error shouldBe null
            game.resolveStack()

            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            game.handSize(2) shouldBe opponentHand + 1
        }

        test("an attacking creature is destroyed without a draw") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(2, "Prophesied End")
                .withLandsOnBattlefield(2, "Plains", 2)
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Plains")
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS).build()

            game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
            val bears = game.findPermanent("Grizzly Bears")!!
            val attackerHand = game.handSize(1)
            game.passPriority()
            game.castSpell(2, "Prophesied End", bears).error shouldBe null
            game.resolveStack()

            game.isInGraveyard(1, "Grizzly Bears") shouldBe true
            game.handSize(1) shouldBe attackerHand
        }
    }
}
