package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Tenured Tethermage (Reality Fracture): "you may sacrifice a land. If you do,
 * create two tapped Heartwood tokens." The Heartwoods are gated on a land actually being sacrificed.
 */
class TenuredTethermageScenarioTest : ScenarioTestBase() {

    private fun builder(): ScenarioBuilder {
        var b = scenario()
            .withPlayers("Player", "Opponent")
            .withCardInHand(1, "Tenured Tethermage")
            .withLandsOnBattlefield(1, "Mountain", 2)
            .withLandsOnBattlefield(1, "Forest", 1)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(4) { b = b.withCardInLibrary(1, "Forest") }
        repeat(4) { b = b.withCardInLibrary(2, "Forest") }
        return b
    }

    init {
        context("Tenured Tethermage") {

            test("sacrificing a land creates two tapped Heartwood tokens") {
                val game = builder().build()
                game.castSpell(1, "Tenured Tethermage").error shouldBe null
                game.resolveStack()

                game.answerYesNo(true).error shouldBe null
                if (game.hasPendingDecision()) {
                    game.selectCards(listOf(game.findPermanents("Forest").first())).error shouldBe null
                }
                game.resolveStack()

                withClue("A land was sacrificed") {
                    game.findPermanents("Mountain").size + game.findPermanents("Forest").size shouldBe 2
                }
                val heartwoods = game.findPermanents("Heartwood")
                withClue("Two Heartwood tokens entered tapped") {
                    heartwoods.size shouldBe 2
                    heartwoods.all { game.state.getEntity(it)?.has<TappedComponent>() == true } shouldBe true
                }
            }

            test("declining the sacrifice creates nothing") {
                val game = builder().build()
                game.castSpell(1, "Tenured Tethermage").error shouldBe null
                game.resolveStack()

                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                game.findPermanents("Heartwood").shouldBeEmpty()
                (game.findPermanents("Mountain").size + game.findPermanents("Forest").size) shouldBe 3
            }
        }
    }
}
