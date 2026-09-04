package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AdderStaffBoggart
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SylvanEchoes
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Sylvan Echoes (LRW #237) — "Whenever you clash and win, you may draw a card."
 *
 * A clash *payoff*, never a clash source, and its ruling is the whole point of the card:
 * "if you win a clash initiated by a spell or ability an opponent controls, the ability will still
 * trigger." So the interesting board is the one where the **opponent's** Adder-Staff Boggart starts
 * the clash and the Echoes controller — who did nothing — wins it and draws. An implementation that
 * only fires the clash event for the clashing spell's controller passes a self-clash test and fails
 * this one.
 *
 * The other half is "and win": a lost clash puts nothing on the stack, so there is no "you may draw"
 * prompt to decline.
 */
class SylvanEchoesScenarioTest : FunSpec({

    val Boulder = com.wingedsheep.sdk.dsl.card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = com.wingedsheep.sdk.dsl.card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(SylvanEchoes, AdderStaffBoggart, Boulder, Pebble))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.answerClash() {
        repeat(4) {
            val decision = pendingDecision as? SelectCardsDecision ?: return
            submitDecision(decision.playerId, CardsSelectedResponse(decision.id, emptyList()))
        }
    }

    /** The active player casts an Adder-Staff Boggart, whose ETB trigger runs the clash. */
    fun GameTestDriver.activePlayerClashes() {
        val cardId = putCardInHand(player1, "Adder-Staff Boggart")
        giveMana(player1, Color.RED, 2)
        castSpell(player1, cardId)
        bothPass()
        bothPass()
        answerClash()
    }

    /** Answer the "you may draw a card" prompt if one is pending; report whether there was one. */
    fun GameTestDriver.answerMayDraw(yes: Boolean): Boolean {
        var guard = 0
        while (guard++ < 6) {
            val decision = pendingDecision
            if (decision is YesNoDecision) {
                submitDecision(decision.playerId, YesNoResponse(decision.id, yes))
                return true
            }
            if (stackSize == 0) return false
            bothPass()
        }
        return false
    }

    fun GameTestDriver.handSize(player: EntityId) = getHandSize(player)

    test("the Echoes controller draws off a clash the opponent started, having won it") {
        val d = driver()
        // Player 2 owns the Echoes and does nothing; player 1's Boggart starts the clash and loses.
        d.putPermanentOnBattlefield(d.player2, "Sylvan Echoes")
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")   // clasher: MV 0
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder")  // Echoes controller: MV 5
        val before = d.handSize(d.player2)

        d.activePlayerClashes()
        val prompted = d.answerMayDraw(yes = true)

        withClue("you can win — and be paid for — a clash you never initiated") {
            prompted shouldBe true
            d.handSize(d.player2) shouldBe before + 1
        }
    }

    test("losing the clash never puts the trigger on the stack") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Sylvan Echoes")
        d.putCardOnTopOfLibrary(d.player1, "Clash Boulder")  // clasher wins
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")   // Echoes controller loses
        val before = d.handSize(d.player2)

        d.activePlayerClashes()
        val prompted = d.answerMayDraw(yes = true)

        withClue("\"and win\" is on the trigger, so a loss asks nothing at all") {
            prompted shouldBe false
            d.handSize(d.player2) shouldBe before
        }
    }

    test("the draw is optional — declining leaves the hand alone") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Sylvan Echoes")
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder")
        val before = d.handSize(d.player2)

        d.activePlayerClashes()
        d.answerMayDraw(yes = false) shouldBe true

        d.handSize(d.player2) shouldBe before
    }
})
