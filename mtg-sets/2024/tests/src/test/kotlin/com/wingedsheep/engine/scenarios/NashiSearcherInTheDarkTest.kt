package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.Nashi
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Scenario for `Nashi, Searcher in the Dark` (DSK 223).
 *
 * Menace; "Whenever Nashi deals combat damage to a player, you mill that many cards. You may put
 * any number of legendary and/or enchantment cards from among them into your hand. If you put no
 * cards into your hand this way, put a +1/+1 counter on Nashi." Built from existing primitives —
 * combat-damage amount drives the mill, the milled cards stay addressable for a filtered
 * "from among them" selection, and the +1/+1 counter is gated on nothing being moved to hand.
 */
class NashiSearcherInTheDarkTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(Nashi)
        d.initMirrorMatch(
            deck = Deck.of("Island" to 30, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    fun plusOneCounters(d: GameTestDriver, entity: EntityId): Int =
        d.state.getEntity(entity)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun attackForTwo(d: GameTestDriver, p1: EntityId, p2: EntityId, nashi: EntityId) {
        d.removeSummoningSickness(nashi)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(nashi), p2)
        d.bothPass()
        d.declareNoBlockers(p2)
        // Keep passing priority so the combat-damage trigger is placed and resolved. It pauses at
        // the "from among them" selection when eligible cards were milled, or (no eligible cards)
        // resolves straight through and adds the +1/+1 counter.
        var guard = 0
        while (d.pendingDecision == null && plusOneCounters(d, nashi) == 0 && guard++ < 12) {
            d.bothPass()
        }
    }

    test("Nashi mills two and may put legendary/enchantment cards from among them into hand") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)

        val nashi = d.putCreatureOnBattlefield(p1, "Nashi, Searcher in the Dark")
        // Stack two enchantments on top so the mill of 2 hits eligible cards.
        val ench1 = d.putCardOnTopOfLibrary(p1, "Test Enchantment")
        val ench2 = d.putCardOnTopOfLibrary(p1, "Test Enchantment")

        attackForTwo(d, p1, p2, nashi)

        // The "from among them" selection — put both enchantments into hand.
        d.submitCardSelection(p1, listOf(ench1, ench2))

        d.getHand(p1).shouldContain(ench1)
        d.getHand(p1).shouldContain(ench2)
        // Cards were put into hand, so no +1/+1 counter.
        plusOneCounters(d, nashi) shouldBe 0
    }

    test("Nashi gets a +1/+1 counter when no cards are put into hand") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)

        val nashi = d.putCreatureOnBattlefield(p1, "Nashi, Searcher in the Dark")
        // Stack two vanilla creatures — neither legendary nor enchantment, so none are eligible.
        d.putCardOnTopOfLibrary(p1, "Grizzly Bears")
        d.putCardOnTopOfLibrary(p1, "Grizzly Bears")

        attackForTwo(d, p1, p2, nashi)

        // If a selection is offered (showAllCards), decline it by selecting nothing.
        if (d.pendingDecision != null) d.submitCardSelection(p1, emptyList())

        plusOneCounters(d, nashi) shouldBe 1
    }
})
