package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class MultiplyByZeroScenarioTest : ScenarioTestBase() {
    init {
        test("zero toughness sends an unmodified creature to the graveyard") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Multiply by Zero")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            game.castSpell(1, "Multiply by Zero", game.findPermanent("Grizzly Bears")!!).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
        }

        test("sets base stats without removing an anthem bonus and expires at cleanup") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Multiply by Zero")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(2, "Glorious Anthem")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withCardInLibrary(1, "Swamp").withCardInLibrary(1, "Swamp").withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Forest").withCardInLibrary(2, "Forest").withCardInLibrary(2, "Forest")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bear = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Multiply by Zero", bear).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(bear) shouldBe 1
            game.state.projectedState.getToughness(bear) shouldBe 1
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            game.state.projectedState.getPower(bear) shouldBe 3
            game.state.projectedState.getToughness(bear) shouldBe 3
        }
    }
}
