package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.OakenBrawler
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
 * Oaken Brawler (LRW #33) — "When this creature enters, clash with an opponent. If you win, put a
 * +1/+1 counter on this creature."
 *
 * Shares Adder-Staff Boggart's shape, so the win/lose gate is covered there; this file leans on the
 * *other* half of CR 701.30a, the "may put that card on the bottom" choice. Each player decides for
 * their own library only, and a card sent to the bottom never leaves the library — a clash that
 * mills or draws would pass every counter assertion and fail these.
 */
class OakenBrawlerScenarioTest : FunSpec({

    val Boulder = com.wingedsheep.sdk.dsl.card("Clash Boulder") {
        manaCost = "{5}"; typeLine = "Artifact"; oracleText = ""
    }
    val Pebble = com.wingedsheep.sdk.dsl.card("Clash Pebble") {
        manaCost = "{0}"; typeLine = "Artifact"; oracleText = ""
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(OakenBrawler, Boulder, Pebble))
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun GameTestDriver.library(player: EntityId) = state.getZone(ZoneKey(player, Zone.LIBRARY))
    fun GameTestDriver.libraryTopName(player: EntityId): String? =
        library(player).firstOrNull()?.let { getCardName(it) }

    fun GameTestDriver.castBrawler(): EntityId {
        val cardId = putCardInHand(player1, "Oaken Brawler")
        giveMana(player1, Color.WHITE, 4)
        castSpell(player1, cardId)
        bothPass()
        bothPass()
        return cardId
    }

    /** Answer both clash prompts; [bottom] decides whether each player buries their own card. */
    fun GameTestDriver.answerClash(bottom: Boolean) {
        repeat(4) {
            val decision = pendingDecision as? SelectCardsDecision ?: return
            val picked = if (bottom) decision.options else emptyList()
            submitDecision(decision.playerId, CardsSelectedResponse(decision.id, picked))
        }
    }

    test("both players may bury their revealed card, and it stays in the library") {
        val d = driver()
        d.putCardOnTopOfLibrary(d.player1, "Clash Boulder")
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")
        val sizeBefore1 = d.library(d.player1).size
        val sizeBefore2 = d.library(d.player2).size

        val brawler = d.castBrawler()
        d.answerClash(bottom = true)

        withClue("bottomed, not drawn or milled — the library keeps every card") {
            d.library(d.player1).size shouldBe sizeBefore1
            d.library(d.player2).size shouldBe sizeBefore2
        }
        d.libraryTopName(d.player1) shouldBe "Plains"
        d.libraryTopName(d.player2) shouldBe "Plains"
        withClue("burying your card afterwards doesn't undo the win it already earned") {
            d.plusOneCounters(brawler) shouldBe 1
        }
    }

    test("declining leaves each revealed card on top of its owner's library") {
        val d = driver()
        d.putCardOnTopOfLibrary(d.player1, "Clash Boulder")
        d.putCardOnTopOfLibrary(d.player2, "Clash Pebble")

        d.castBrawler()
        d.answerClash(bottom = false)

        d.libraryTopName(d.player1) shouldBe "Clash Boulder"
        d.libraryTopName(d.player2) shouldBe "Clash Pebble"
    }
})
