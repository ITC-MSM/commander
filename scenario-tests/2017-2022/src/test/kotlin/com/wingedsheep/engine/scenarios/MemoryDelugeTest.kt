package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mid.cards.MemoryDeluge
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Memory Deluge ({2}{U}{U}, instant, flashback {5}{U}{U}):
 *  "Look at the top X cards of your library, where X is the amount of mana spent
 *   to cast this spell. Put two of them into your hand and the rest on the bottom
 *   of your library in a random order."
 *
 * Verifies the [DynamicAmount.TotalManaSpent][com.wingedsheep.sdk.scripting.values.DynamicAmount.TotalManaSpent]
 * plumbing: X = 4 on hardcast, X = 7 on flashback.
 */
class MemoryDelugeTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(MemoryDeluge)
        return driver
    }

    test("hardcast for {2}{U}{U}: looks at top 4 cards") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val top1 = driver.putCardOnTopOfLibrary(activePlayer, "Forest")
        val top2 = driver.putCardOnTopOfLibrary(activePlayer, "Plains")
        val top3 = driver.putCardOnTopOfLibrary(activePlayer, "Mountain")
        val top4 = driver.putCardOnTopOfLibrary(activePlayer, "Swamp")
        // Library top → bottom: top4, top3, top2, top1, <deck>

        val spell = driver.putCardInHand(activePlayer, "Memory Deluge")
        driver.giveMana(activePlayer, Color.BLUE, 4)

        driver.castSpell(activePlayer, spell).isSuccess shouldBe true
        driver.bothPass()

        driver.isPaused shouldBe true
        val select = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        select.options.size shouldBe 4
        select.minSelections shouldBe 2
        select.maxSelections shouldBe 2
        select.options.toSet() shouldBe setOf(top1, top2, top3, top4)

        // Keep top4 + top2; the other two go to the bottom in random order.
        driver.submitCardSelection(activePlayer, listOf(top4, top2))
        driver.isPaused shouldBe false

        val hand = driver.state.getZone(ZoneKey(activePlayer, Zone.HAND))
        hand.contains(top4) shouldBe true
        hand.contains(top2) shouldBe true

        val library = driver.state.getZone(ZoneKey(activePlayer, Zone.LIBRARY))
        val tail = library.takeLast(2).toSet()
        tail shouldBe setOf(top1, top3)
    }

    test("flashback for {5}{U}{U}: looks at top 7 cards") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        repeat(7) { driver.putCardOnTopOfLibrary(activePlayer, "Forest") }
        val expectedSize = 7

        val spell = driver.putCardInGraveyard(activePlayer, "Memory Deluge")
        driver.giveMana(activePlayer, Color.BLUE, 7)

        val cast = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = spell,
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        cast.isSuccess shouldBe true
        driver.bothPass()

        driver.isPaused shouldBe true
        val select = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        select.options.size shouldBe expectedSize
        select.minSelections shouldBe 2
        select.maxSelections shouldBe 2
    }
})
