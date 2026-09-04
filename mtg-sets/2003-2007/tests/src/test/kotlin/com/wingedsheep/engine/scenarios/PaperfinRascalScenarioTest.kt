package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.PaperfinRascal
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Paperfin Rascal (LRW #77) — "When this creature enters, clash with an opponent. If you win, put a
 * +1/+1 counter on this creature."
 *
 * The third of the clash-for-a-counter commons, and the one that takes the empty-library edge.
 * CR 701.30d asks for a card with a *greater* mana value than every other card revealed; a player
 * with no library reveals nothing, and nothing has no mana value. So an empty library can never
 * win — but it also puts up no card for the other player to beat, so the other player wins with
 * anything at all, a {0} artifact included. The naive "compare two numbers, default the missing one
 * to 0" implementation ties instead, and this pair of tests is what separates them.
 */
class PaperfinRascalScenarioTest : FunSpec({

    val Pebble = com.wingedsheep.sdk.dsl.card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(PaperfinRascal, Pebble))
        d.initMirrorMatch(deck = Deck.of("Island" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun GameTestDriver.emptyLibrary(player: EntityId) {
        val key = ZoneKey(player, Zone.LIBRARY)
        replaceState(state.copy(zones = state.zones + (key to emptyList())))
    }

    fun GameTestDriver.castRascal(): EntityId {
        val cardId = putCardInHand(player1, "Paperfin Rascal")
        giveMana(player1, Color.BLUE, 3)
        castSpell(player1, cardId)
        bothPass()
        bothPass()
        return cardId
    }

    fun GameTestDriver.answerClash() {
        repeat(4) {
            val decision = pendingDecision as? SelectCardsDecision ?: return
            submitDecision(decision.playerId, CardsSelectedResponse(decision.id, emptyList()))
        }
    }

    test("a {0} card beats an opponent who has no library at all") {
        val d = driver()
        // The Rascal's controller keeps the cheapest possible card; the opponent reveals nothing.
        d.putCardOnTopOfLibrary(d.player1, "Clash Pebble")
        d.emptyLibrary(d.player2)

        val rascal = d.castRascal()
        d.answerClash()

        withClue("mana value 0 still beats no card at all — nothing has no mana value") {
            d.plusOneCounters(rascal) shouldBe 1
        }
    }

    test("an empty library of your own cannot win, even against a {0}") {
        val d = driver()
        d.emptyLibrary(d.player1)
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")

        val rascal = d.castRascal()
        d.answerClash()

        d.plusOneCounters(rascal) shouldBe 0
    }
})
