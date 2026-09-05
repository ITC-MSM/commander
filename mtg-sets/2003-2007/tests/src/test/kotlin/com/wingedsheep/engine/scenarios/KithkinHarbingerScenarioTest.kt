package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.LibrarySearchedEvent
import com.wingedsheep.engine.core.LibraryShuffledEvent
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.KithkinHarbinger
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class KithkinHarbingerScenarioTest : FunSpec({
    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(KithkinHarbinger))
        initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun GameTestDriver.castHarbinger() {
        val source = putCardInHand(player1, "Kithkin Harbinger")
        giveMana(player1, Color.WHITE, 3)
        castSpell(player1, source).error shouldBe null
        bothPass().error shouldBe null
        bothPass().error shouldBe null
        pendingDecision.shouldBeInstanceOf<YesNoDecision>()
    }

    test("declining the search preserves library order and emits no search or shuffle") {
        val d = driver()
        d.putCardOnTopOfLibrary(d.player1, "Kithkin Harbinger")
        d.castHarbinger()
        val before = d.state.getZone(ZoneKey(d.player1, Zone.LIBRARY)).toList()
        val eventCount = d.events.size

        d.submitYesNo(d.player1, false).error shouldBe null

        d.pendingDecision shouldBe null
        d.state.getZone(ZoneKey(d.player1, Zone.LIBRARY)) shouldBe before
        d.events.drop(eventCount).filterIsInstance<LibrarySearchedEvent>().size shouldBe 0
        d.events.drop(eventCount).filterIsInstance<LibraryShuffledEvent>().size shouldBe 0
    }

    test("accepting the search reveals the chosen tribal card and puts it on top") {
        val d = driver()
        val found = d.putCardOnTopOfLibrary(d.player1, "Kithkin Harbinger")
        d.putCardOnTopOfLibrary(d.player1, "Plains")
        d.castHarbinger()
        val eventCount = d.events.size

        d.submitYesNo(d.player1, true).error shouldBe null
        d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>().options.contains(found) shouldBe true
        d.submitCardSelection(d.player1, listOf(found)).error shouldBe null

        d.pendingDecision shouldBe null
        d.state.getZone(ZoneKey(d.player1, Zone.LIBRARY)).first() shouldBe found
        d.events.drop(eventCount).filterIsInstance<LibrarySearchedEvent>().size shouldBe 1
        d.events.drop(eventCount).filterIsInstance<LibraryShuffledEvent>().size shouldBe 1
    }

    test("accepting but finding no card still searches and shuffles") {
        val d = driver()
        d.putCardOnTopOfLibrary(d.player1, "Kithkin Harbinger")
        d.castHarbinger()
        val eventCount = d.events.size

        d.submitYesNo(d.player1, true).error shouldBe null
        d.submitCardSelection(d.player1, emptyList()).error shouldBe null

        d.pendingDecision shouldBe null
        d.events.drop(eventCount).filterIsInstance<LibrarySearchedEvent>().size shouldBe 1
        d.events.drop(eventCount).filterIsInstance<LibraryShuffledEvent>().size shouldBe 1
    }
})
