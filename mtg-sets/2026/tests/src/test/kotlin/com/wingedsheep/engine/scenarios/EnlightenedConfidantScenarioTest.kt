package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Enlightened Confidant (FRA #5) — lifelink; at your end step, if you gained life this turn,
 * surveil 1, and a card surveilled into the graveyard with mana value ≤ the life gained this turn
 * goes to your hand.
 */
class EnlightenedConfidantScenarioTest : ScenarioTestBase() {

    init {
        /** Attacks with the Confidant (2 lifelink damage → 2 life gained) and reaches the end step. */
        fun endStepAfterAttack(libraryTop: String, attack: Boolean = true): TestGame {
            var b = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Enlightened Confidant")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            repeat(3) { b = b.withCardInLibrary(1, libraryTop) }
            repeat(3) { b = b.withCardInLibrary(2, "Island") }
            val game = b.build()
            if (attack) {
                game.declareAttackers(mapOf("Enlightened Confidant" to 2)).error shouldBe null
            }
            game.passUntilPhase(Phase.ENDING, Step.END)
            game.resolveStack()
            return game
        }

        context("Enlightened Confidant") {
            test("a surveilled card with mana value at most the life gained goes to hand") {
                val game = endStepAfterAttack("Grizzly Bears") // mana value 2, gained 2
                game.getLifeTotal(1) shouldBe 22

                val surveil = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                game.selectCards(surveil.options).error shouldBe null
                game.resolveStack()

                game.isInHand(1, "Grizzly Bears") shouldBe true
                game.isInGraveyard(1, "Grizzly Bears") shouldBe false
            }

            test("a card with greater mana value stays in the graveyard") {
                val game = endStepAfterAttack("Hill Giant") // mana value 4, gained 2

                val surveil = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                game.selectCards(surveil.options).error shouldBe null
                game.resolveStack()

                game.isInGraveyard(1, "Hill Giant") shouldBe true
                game.isInHand(1, "Hill Giant") shouldBe false
            }

            test("a card kept on top is not put into hand") {
                val game = endStepAfterAttack("Grizzly Bears")

                game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                game.skipSelection().error shouldBe null
                game.resolveStack()

                game.handSize(1) shouldBe 0
                game.librarySize(1) shouldBe 3
            }

            test("without life gained this turn there is no trigger") {
                val game = endStepAfterAttack("Grizzly Bears", attack = false)

                withClue("the intervening-if fails, so there is no surveil") {
                    game.hasPendingDecision() shouldBe false
                }
                game.librarySize(1) shouldBe 3
                game.graveyardSize(1) shouldBe 0
            }
        }
    }
}
