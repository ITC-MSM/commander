package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Holy Cow (OTJ #16).
 *
 * Holy Cow — {2}{W} Creature — Ox Angel, 2/2.
 *   "Flash. Flying. When this creature enters, you gain 2 life and scry 1."
 */
class HolyCowScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Holy Cow ETB") {

            fun buildGame() = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Holy Cow")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(1, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            test("gains 2 life and scrys 1 on enter") {
                val game = buildGame()
                val startLife = game.getLifeTotal(1)

                game.castSpell(1, "Holy Cow").error shouldBe null
                game.resolveStack() // ETB: gain 2 life, then scry 1 (SelectCardsDecision)

                withClue("controller gained 2 life") {
                    game.getLifeTotal(1) shouldBe startLife + 2
                }
                val decision = game.getPendingDecision()
                withClue("scry 1 presents a single-card selection over the top card") {
                    (decision is SelectCardsDecision) shouldBe true
                    (decision as SelectCardsDecision).options.size shouldBe 1
                }
                // Bottom the looked-at card.
                game.selectCards((decision as SelectCardsDecision).options).error shouldBe null
                game.resolveStack()

                withClue("library still has both cards (scry bottoms, doesn't remove)") {
                    game.librarySize(1) shouldBe 2
                }
                withClue("Holy Cow resolved onto the battlefield") {
                    game.isOnBattlefield("Holy Cow") shouldBe true
                }
            }

            test("Holy Cow has flying once it resolves") {
                val game = buildGame()
                game.castSpell(1, "Holy Cow").error shouldBe null
                game.resolveStack()
                if (game.hasPendingDecision()) game.skipSelection() // keep card on top
                game.resolveStack()

                val cow = game.findPermanent("Holy Cow")!!
                projector.hasProjectedKeyword(game.state, cow, Keyword.FLYING) shouldBe true
            }
        }
    }
}
