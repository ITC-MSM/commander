package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Rise of the Deathbringer (FRA #63) — the draw mode loses life equal to the cards *actually*
 * drawn, which falls short of the greatest power when the library runs out.
 */
class RiseOfTheDeathbringerScenarioTest : ScenarioTestBase() {

    init {
        fun builder(librarySize: Int): ScenarioBuilder {
            var b = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Rise of the Deathbringer")
                .withLandsOnBattlefield(1, "Swamp", 5)
                .withCardOnBattlefield(1, "Hill Giant")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            repeat(librarySize) { b = b.withCardInLibrary(1, "Swamp") }
            return b
        }

        context("Rise of the Deathbringer") {
            test("draws cards equal to the greatest power and loses that much life") {
                // One card was already drawn this turn — the loss counts only this spell's draws.
                val game = builder(librarySize = 6).withCardsDrawnThisTurn(1, 1).build()
                val handBefore = game.handSize(1)

                game.castSpellWithMode(1, "Rise of the Deathbringer", 0).error shouldBe null
                game.resolveStack()

                withClue("Hill Giant's power is 3") { game.handSize(1) shouldBe handBefore - 1 + 3 }
                game.getLifeTotal(1) shouldBe 17
            }

            test("a short library loses life only for the cards actually drawn") {
                val game = builder(librarySize = 2).build()

                game.castSpellWithMode(1, "Rise of the Deathbringer", 0).error shouldBe null
                game.resolveStack()

                game.librarySize(1) shouldBe 0
                game.getLifeTotal(1) shouldBe 18
            }

            test("the other mode gives all creatures -3/-3") {
                val game = builder(librarySize = 1).build()

                game.castSpellWithMode(1, "Rise of the Deathbringer", 1).error shouldBe null
                game.resolveStack()

                game.findPermanents("Grizzly Bears").size shouldBe 0
                game.isOnBattlefield("Hill Giant") shouldBe false
                game.getLifeTotal(1) shouldBe 20
            }
        }
    }
}
