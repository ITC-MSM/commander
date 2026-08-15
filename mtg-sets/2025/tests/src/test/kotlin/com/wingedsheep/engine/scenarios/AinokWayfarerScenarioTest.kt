package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Ainok Wayfarer (TDM #134).
 *
 * "When this creature enters, mill three cards. You may put a land card from among them
 *  into your hand. If you don't, put a +1/+1 counter on this creature."
 *
 * Verifies both branches of the composed ETB pipeline:
 *  - a land is milled and taken into hand → no counter on Ainok,
 *  - no land is milled (or the player declines) → a +1/+1 counter goes on Ainok.
 */
class AinokWayfarerScenarioTest : ScenarioTestBase() {

    init {
        context("Ainok Wayfarer") {

            test("milling a land lets you take it into hand and adds no counter") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Ainok Wayfarer")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Top three of library are lands so a land is among the milled cards.
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                val handBefore = game.handSize(1)

                val cast = game.castSpell(1, "Ainok Wayfarer")
                withClue("Casting Ainok Wayfarer should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // ETB prompts to put a land into hand; take the first offered land.
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                game.selectCards(listOf(decision.options.first()))
                game.resolveStack()

                withClue("Ainok Wayfarer should be on the battlefield") {
                    game.isOnBattlefield("Ainok Wayfarer") shouldBe true
                }
                // Three milled, one Forest returned to hand → graveyard holds two Forests.
                withClue("Two of the three milled Forests should remain in the graveyard") {
                    game.findCardsInGraveyard(1, "Forest").size shouldBe 2
                }
                // The taken land replaces Ainok Wayfarer in hand (cast one, drew one back).
                withClue("Hand size should be unchanged: cast Ainok, took a land back") {
                    game.handSize(1) shouldBe handBefore
                }

                val ainokId = game.findPermanent("Ainok Wayfarer")!!
                val counters = game.state.getEntity(ainokId)?.get<CountersComponent>()
                withClue("Ainok should have no +1/+1 counter when a land was taken") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 0
                }
            }

            test("milling no land puts a +1/+1 counter on Ainok") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Ainok Wayfarer")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Top three of library are non-lands, so no land can be taken.
                repeat(3) { builder = builder.withCardInLibrary(1, "Grizzly Bears") }
                val game = builder.build()

                val cast = game.castSpell(1, "Ainok Wayfarer")
                withClue("Casting Ainok Wayfarer should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // If the engine surfaces an (empty / decline) selection, skip it.
                if (game.hasPendingDecision()) {
                    game.skipSelection()
                    game.resolveStack()
                }

                withClue("Ainok Wayfarer should be on the battlefield") {
                    game.isOnBattlefield("Ainok Wayfarer") shouldBe true
                }
                withClue("All three milled non-lands should be in the graveyard") {
                    game.findCardsInGraveyard(1, "Grizzly Bears").size shouldBe 3
                }

                val ainokId = game.findPermanent("Ainok Wayfarer")!!
                val counters = game.state.getEntity(ainokId)?.get<CountersComponent>()
                withClue("Ainok should have a +1/+1 counter when no land was taken") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
                }
            }
        }
    }
}
