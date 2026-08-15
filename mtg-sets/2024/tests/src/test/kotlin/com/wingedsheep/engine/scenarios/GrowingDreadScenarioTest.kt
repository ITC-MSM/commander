package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TurnFaceUp
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
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
 * Growing Dread (DSK #216) — {G}{U} Enchantment with Flash.
 *
 *  - When this enchantment enters, manifest dread.
 *  - Whenever you turn a permanent face up, put a +1/+1 counter on it.
 *
 * Exercises the enters trigger (shared manifest-dread recipe) and the face-up payoff: turning a
 * manifested creature face up puts a +1/+1 counter on that creature (the triggering permanent),
 * so it ends up as its printed P/T plus one.
 */
class GrowingDreadScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    test("entering manifests dread, then turning the manifest face up adds a +1/+1 counter") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Top two: a creature on top (to manifest), a land beneath.
        d.putCardOnTopOfLibrary(you, "Forest")
        val creature = d.putCardOnTopOfLibrary(you, "Centaur Courser") // {2}{G} 3/3

        // Cast Growing Dread; its ETB manifest-dread trigger resolves and pauses on the pick.
        val gd = d.putCardInHand(you, "Growing Dread")
        d.giveMana(you, Color.GREEN, 1)
        d.giveMana(you, Color.BLUE, 1)
        d.castSpell(you, gd)
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitDecision(you, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(creature)))
        d.state.getEntity(creature)?.get<FaceDownComponent>() shouldBe FaceDownComponent

        // Turn the manifested Centaur Courser face up for its {2}{G} cost.
        d.giveMana(you, Color.GREEN, 3)
        d.submit(TurnFaceUp(playerId = you, sourceId = creature, paymentStrategy = PaymentStrategy.FromPool))
            .error shouldBe null
        // Drain the "you turn a permanent face up" trigger.
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Growing Dread's trigger put a +1/+1 counter on it — now a 4/4.
        d.state.getEntity(creature)?.get<FaceDownComponent>() shouldBe null
        val counters = d.state.getEntity(creature)?.get<CountersComponent>()?.counters ?: emptyMap()
        counters[CounterType.PLUS_ONE_PLUS_ONE] shouldBe 1
        d.state.projectedState.getPower(creature) shouldBe 4
        d.state.projectedState.getToughness(creature) shouldBe 4
    }
})
