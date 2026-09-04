package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AdderStaffBoggart
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Adder-Staff Boggart (LRW #148) — "When this creature enters, clash with an opponent. If you win,
 * put a +1/+1 counter on this creature."
 *
 * The clash mechanic itself is proved in `ClashScenarioTest`; what this file pins is that the
 * *card* is wired to it — the ETB trigger runs a real clash (both players get their top-or-bottom
 * prompt) and the counter is gated on winning it, not placed unconditionally. The rigged library
 * tops are what make the outcome deterministic: a {5} artifact against a {0} artifact.
 */
class AdderStaffBoggartScenarioTest : FunSpec({

    // {5} and {0} fillers, so the clash outcome is decided by the rigged top of each library and
    // never by whatever the shuffled deck happened to leave there.
    val Boulder = com.wingedsheep.sdk.dsl.card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = com.wingedsheep.sdk.dsl.card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(AdderStaffBoggart, Boulder, Pebble))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    /** Answer both clash prompts, leaving each revealed card on top. */
    fun GameTestDriver.answerClash(): List<EntityId> {
        val asked = mutableListOf<EntityId>()
        repeat(4) {
            val decision = pendingDecision as? SelectCardsDecision ?: return asked
            asked += decision.playerId
            submitDecision(decision.playerId, CardsSelectedResponse(decision.id, emptyList()))
        }
        return asked
    }

    /** Cast the Boggart and let its ETB trigger start resolving. */
    fun GameTestDriver.castBoggart(): EntityId {
        val cardId = putCardInHand(player1, "Adder-Staff Boggart")
        giveMana(player1, Color.RED, 2)
        castSpell(player1, cardId)
        bothPass() // creature resolves, ETB trigger goes on the stack
        bothPass() // trigger resolves into the clash
        return cardId
    }

    test("winning the clash puts a +1/+1 counter on it, and both players were asked") {
        val d = driver()
        d.putCardOnTopOfLibrary(d.player1, "Clash Boulder") // MV 5
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")  // MV 0

        val boggart = d.castBoggart()
        val asked = d.answerClash()

        withClue("clash is a two-player action — the opponent gets their own top-or-bottom choice") {
            asked shouldBe listOf(d.player1, d.player2)
        }
        d.plusOneCounters(boggart) shouldBe 1
    }

    test("losing the clash leaves it a 2/1 with no counter") {
        val d = driver()
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")  // MV 0
        d.putCardOnTopOfLibrary(d.player2, "Clash Boulder") // MV 5

        val boggart = d.castBoggart()
        d.answerClash()

        d.plusOneCounters(boggart) shouldBe 0
    }

    test("a tie is not a win — CR 701.30d wants a strictly greater mana value") {
        val d = driver()
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")

        val boggart = d.castBoggart()
        d.answerClash()

        d.plusOneCounters(boggart) shouldBe 0
    }
})
