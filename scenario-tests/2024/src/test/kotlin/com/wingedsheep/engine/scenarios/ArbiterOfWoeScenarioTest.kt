package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Arbiter of Woe (FDN) — {4}{B}{B} 5/4 Demon.
 * Additional cost: sacrifice a creature. Flying.
 * ETB: each opponent discards a card and loses 2 life; you draw a card and gain 2 life.
 *
 * Exercises the additional sacrifice cost plus the composed ETB drain
 * (EachOpponentDiscards + per-opponent LoseLife + your DrawCards/GainLife).
 */
class ArbiterOfWoeScenarioTest : ScenarioTestBase() {

    init {
        context("Arbiter of Woe") {

            test("sacrifices a creature on cast and drains each opponent on entry") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Arbiter of Woe")
                    // Fodder for the additional sacrifice cost.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardInLibrary(1, "Swamp")
                    // Exactly one card so the opponent's discard is forced (no decision).
                    .withCardInHand(2, "Swamp")
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellWithAdditionalSacrifice(1, "Arbiter of Woe", "Grizzly Bears")
                    .error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears was sacrificed to pay the additional cost") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
                game.isOnBattlefield("Arbiter of Woe") shouldBe true

                withClue("each opponent loses 2 life") { game.getLifeTotal(2) shouldBe 18 }
                withClue("each opponent discards a card") { game.handSize(2) shouldBe 0 }
                withClue("you gain 2 life") { game.getLifeTotal(1) shouldBe 22 }
                // Started with just Arbiter in hand → cast empties hand → draw one card.
                withClue("you draw a card") { game.handSize(1) shouldBe 1 }
            }
        }
    }
}
