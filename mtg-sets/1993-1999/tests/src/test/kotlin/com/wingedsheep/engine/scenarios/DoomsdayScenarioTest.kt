package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.wth.cards.Doomsday
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

/**
 * Doomsday (WTH #66) — {B}{B}{B} Sorcery.
 *
 *   "Search your library and graveyard for five cards and exile the rest. Put the chosen cards on
 *    top of your library in any order. You lose half your life, rounded up."
 *
 * The three things worth proving are the ones the composition is carrying: the search pool spans
 * *both* zones at once, the un-chosen cards from both zones are exiled rather than shuffled back,
 * and the "fewer than five cards" case (2018-03-16 ruling) leaves everything in the library
 * instead of stalling on a choice that can't be made.
 */
class DoomsdayScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + Doomsday)
        initMirrorMatch(deck = Deck.of("Swamp" to 60), startingLife = 20)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    /** Answer the choose-five and the order-on-top decisions until the spell finishes resolving. */
    fun GameTestDriver.resolveDoomsday(pick: (SelectCardsDecision) -> List<com.wingedsheep.sdk.model.EntityId>) {
        var guard = 0
        while (guard++ < 20) {
            when (val decision = pendingDecision) {
                is SelectCardsDecision -> submitCardSelection(decision.playerId, pick(decision))
                is ReorderLibraryDecision ->
                    submitOrderedResponse(decision.playerId, decision.cards)
                null -> if (state.stack.isNotEmpty()) bothPass() else return
                else -> return
            }
        }
    }

    test("chooses five cards across library and graveyard, exiles the rest, and loses half life") {
        val d = driver()
        val me = d.activePlayer!!

        // Three cards in the graveyard so the pool is provably both zones.
        repeat(3) { d.putCardInGraveyard(me, "Swamp") }
        val libraryBefore = d.state.getLibrary(me).size
        libraryBefore shouldBeGreaterThan 5
        val poolBefore = libraryBefore + 3

        val doomsday = d.putCardInHand(me, "Doomsday")
        d.giveMana(me, Color.BLACK, 3)
        d.castSpell(me, doomsday).error shouldBe null
        d.bothPass()

        // The look spans library + graveyard.
        val choice = d.pendingDecision as SelectCardsDecision
        choice.options.size shouldBe poolBefore
        choice.minSelections shouldBe 5
        choice.maxSelections shouldBe 5

        d.resolveDoomsday { it.options.take(5) }

        // Exactly the five chosen cards remain, on top of an otherwise empty library.
        d.state.getLibrary(me).size shouldBe 5
        // Everything else from both zones is exiled; the graveyard holds only Doomsday itself.
        d.getExile(me).size shouldBe (poolBefore - 5)
        d.getGraveyardCardNames(me) shouldBe listOf("Doomsday")
        // 20 life, halved and rounded up, is 10 lost.
        d.getLifeTotal(me) shouldBe 10
    }

    test("with fewer than five cards between the zones, all of them wind up in the library") {
        val d = driver()
        val me = d.activePlayer!!

        // Trim the library to two cards and put one in the graveyard: three in the pool.
        val keep = d.state.getLibrary(me).take(2)
        d.replaceState(
            d.state.copy(zones = d.state.zones + (ZoneKey(me, Zone.LIBRARY) to keep))
        )
        d.putCardInGraveyard(me, "Swamp")

        val doomsday = d.putCardInHand(me, "Doomsday")
        d.giveMana(me, Color.BLACK, 3)
        d.castSpell(me, doomsday).error shouldBe null
        d.bothPass()
        d.resolveDoomsday { it.options }

        // "You can't choose to find fewer than five" auto-resolves to "find all of them" —
        // nothing is exiled and the graveyard card is now in the library too.
        d.state.getLibrary(me).size shouldBe 3
        d.getExile(me).size shouldBe 0
        d.getGraveyardCardNames(me) shouldBe listOf("Doomsday")
        d.getLifeTotal(me) shouldBe 10
    }
})
