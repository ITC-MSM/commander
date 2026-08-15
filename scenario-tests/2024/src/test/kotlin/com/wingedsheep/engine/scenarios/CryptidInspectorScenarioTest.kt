package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TurnFaceUp
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Cryptid Inspector (DSK #174) — {2}{G} Elf Warrior 2/3 with Vigilance.
 *
 * "Whenever a face-down permanent you control enters and whenever this creature or another
 *  permanent you control is turned face up, put a +1/+1 counter on this creature."
 *
 * Two distinct triggers, both adding a +1/+1 counter to the Inspector itself: one when a
 * face-down permanent you control enters, one when a permanent you control is turned face up.
 */
class CryptidInspectorScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    fun GameTestDriver.counterOn(id: com.wingedsheep.sdk.model.EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("a face-down permanent entering and a face-up turn each add a +1/+1 counter to the Inspector") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val inspector = d.putCreatureOnBattlefield(you, "Cryptid Inspector")
        d.counterOn(inspector) shouldBe 0

        // Cast Manifest Dread: a face-down 2/2 enters → first trigger adds a counter.
        d.putCardOnTopOfLibrary(you, "Forest")
        val manifested = d.putCardOnTopOfLibrary(you, "Centaur Courser")
        val md = d.putCardInHand(you, "Manifest Dread")
        d.giveMana(you, Color.GREEN, 2)
        d.castSpell(you, md)
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()
        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitDecision(you, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(manifested)))
        // Drain the face-down-enters trigger.
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.counterOn(inspector) shouldBe 1
        d.state.projectedState.getPower(inspector) shouldBe 3 // base 2 +1

        // Turn that manifested creature face up → the turn-face-up trigger adds another counter.
        d.giveMana(you, Color.GREEN, 3)
        d.submit(TurnFaceUp(playerId = you, sourceId = manifested, paymentStrategy = PaymentStrategy.FromPool))
            .error shouldBe null
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.counterOn(inspector) shouldBe 2
        d.state.projectedState.getPower(inspector) shouldBe 4
        d.state.projectedState.getToughness(inspector) shouldBe 5
    }
})
