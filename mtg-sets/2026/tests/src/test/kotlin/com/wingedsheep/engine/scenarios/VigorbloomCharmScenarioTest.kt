package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class VigorbloomCharmScenarioTest : ScenarioTestBase() {
    private fun board() = scenario().withPlayers()
        .withCardInHand(1, "Vigorbloom Charm")
        .withCardOnBattlefield(1, "Grizzly Bears")
        .withCardOnBattlefield(2, "Goblin Piker")
        .withCardInLibrary(1, "Forest")
        .withLandsOnBattlefield(1, "Forest", 1)
        .withLandsOnBattlefield(1, "Plains", 1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

    init {
        test("grants hexproof and indestructible to a permanent you control") {
            val game = board()
            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpellWithMode(1, "Vigorbloom Charm", 0, bears).error shouldBe null
            game.resolveStack()
            game.state.projectedState.hasKeyword(bears, Keyword.HEXPROOF) shouldBe true
            game.state.projectedState.hasKeyword(bears, Keyword.INDESTRUCTIBLE) shouldBe true
        }

        test("draws a card and gains 3 life") {
            val game = board()
            game.castSpellWithMode(1, "Vigorbloom Charm", 1).error shouldBe null
            game.resolveStack()
            game.handSize(1) shouldBe 1
            game.getLifeTotal(1) shouldBe 23
        }

        test("the counter lands before the fight, so the 3/3 Bears survives the Goblin Piker it kills") {
            val game = board()
            val bears = game.findPermanent("Grizzly Bears")!!
            val piker = game.findPermanent("Goblin Piker")!!
            val targets = listOf(ChosenTarget.Permanent(bears), ChosenTarget.Permanent(piker))
            game.execute(
                CastSpell(
                    game.player1Id,
                    game.findCardsInHand(1, "Vigorbloom Charm").single(),
                    targets,
                    chosenModes = listOf(2),
                    modeTargetsOrdered = listOf(targets)
                )
            ).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Goblin Piker") shouldBe true
            game.isOnBattlefield("Grizzly Bears") shouldBe true
        }
    }
}
