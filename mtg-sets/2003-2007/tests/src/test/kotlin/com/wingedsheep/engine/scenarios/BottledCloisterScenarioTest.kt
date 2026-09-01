package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.BottledCloister
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Bottled Cloister — {4} Artifact (Ravnica: City of Guilds #256)
 *
 * "At the beginning of each opponent's upkeep, exile all cards from your hand face down.
 *  At the beginning of your upkeep, return all cards you own exiled with this artifact to your
 *  hand, then draw a card."
 *
 * The two halves are a linked pair (CR 607) — the exile writes the Cloister's own
 * `LinkedExileComponent` and the upkeep half reads that same pile back. What needs proving is
 * that the round trip closes: the hand really leaves face down on the opponent's upkeep, and the
 * same cards really come back (plus a draw) on yours — and that when the Cloister is gone, so is
 * the way home.
 */
class BottledCloisterScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + BottledCloister)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("the hand is exiled face down on the opponent's upkeep and returned on yours, plus a draw") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player1, "Bottled Cloister")
        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")

        // Player 2's upkeep — passPriorityUntil stops the moment the step begins, so the trigger
        // is still on the stack until both players pass.
        d.passPriorityUntil(Step.UPKEEP)
        d.bothPass()

        // Measured here rather than at setup: player 1's own cleanup step discarded down to seven
        // on the way past, so the pile is whatever was actually in hand at the opponent's upkeep.
        val exiled = d.state.getExile(d.player1).size

        withClue("\"exile all cards from your hand\" — the Cloister's controller's hand empties") {
            d.getHandSize(d.player1) shouldBe 0
            (exiled > 0) shouldBe true
        }
        withClue("face down, so the opponent can't read what was put away") {
            d.state.getEntity(bolt)?.has<FaceDownComponent>() shouldBe true
        }
        withClue("the opponent's own hand is untouched") {
            (d.getHandSize(d.player2) > 0) shouldBe true
        }

        // Player 1's next upkeep.
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.passPriorityUntil(Step.UPKEEP)
        d.bothPass()

        withClue("the whole linked pile comes back, and \"then draw a card\" adds one more") {
            d.getHandSize(d.player1) shouldBe exiled + 1
            d.state.getExile(d.player1).size shouldBe 0
        }
        withClue("and it is a real card in hand again, not a face-down one") {
            (bolt in d.state.getHand(d.player1)) shouldBe true
            d.state.getEntity(bolt)?.has<FaceDownComponent>() shouldBe false
        }
    }

    test("cards exiled with a Cloister that has left the battlefield stay exiled") {
        val d = driver()
        val cloister = d.putPermanentOnBattlefield(d.player1, "Bottled Cloister")
        d.putCardInHand(d.player1, "Lightning Bolt")

        d.passPriorityUntil(Step.UPKEEP)
        d.bothPass()
        d.getHandSize(d.player1) shouldBe 0
        val exiled = d.state.getExile(d.player1).size

        // The return trigger lives on the Cloister; with it gone nothing reads the pile back.
        d.moveToGraveyard(cloister)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.passPriorityUntil(Step.UPKEEP)
        d.bothPass()

        withClue("nothing came home — with no Cloister there is no trigger to read the pile back") {
            d.getExileCardNames(d.player1).contains("Lightning Bolt") shouldBe true
            d.state.getExile(d.player1).size shouldBe exiled
        }
    }
})
