package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tmt.cards.RaphaelsTechnique
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Raphael's Technique (TMT #105) — "Each player may discard their hand and draw seven cards."
 *
 * Both players accept: each discards their whole hand and draws a fresh seven.
 */
class RaphaelsTechniqueTest : FunSpec({
    test("each player who accepts discards their hand and draws seven") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RaphaelsTechnique))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val raph = driver.putCardInHand(player, "Raphael's Technique")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveMana(player, Color.RED, 6) // {4}{R}{R}
        driver.castSpell(player, raph, emptyList()).isSuccess shouldBe true

        // Resolve the spell, accepting each per-player "may" prompt.
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 40) {
            val decision = driver.pendingDecision
            if (decision is YesNoDecision) {
                driver.submitYesNo(decision.playerId, true)
            } else {
                driver.bothPass()
            }
        }

        driver.getHandSize(player) shouldBe 7
        driver.getHandSize(opponent) shouldBe 7
    }
})
