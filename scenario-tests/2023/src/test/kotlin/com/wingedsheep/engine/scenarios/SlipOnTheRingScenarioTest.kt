package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.player.TheRingComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Slip On the Ring — "Exile target creature you own, then return it to the battlefield under your
 * control. The Ring tempts you." The target is restricted to a creature you own, so returning it
 * under its owner's control IS under your control. Exercises the flicker (a new object returns) plus
 * the Ring-tempts rider.
 */
class SlipOnTheRingScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
    }

    test("exiles and returns your creature under your control, then tempts you with the Ring") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val before = d.putCreatureOnBattlefield(you, "Grizzly Bears")
        val slip = d.putCardInHand(you, "Slip On the Ring")
        d.giveMana(you, Color.WHITE, 2) // {1}{W}

        d.castSpell(you, slip, listOf(before)).isSuccess shouldBe true
        d.bothPass() // resolve: exile → return → pause to choose a Ring-bearer

        // The creature returned to the battlefield under your control, and is a legal Ring-bearer
        // (it exists when the Ring tempts you, i.e. after the exile→return resolved).
        val after = d.findPermanent(you, "Grizzly Bears")
        after shouldNotBe null

        val decision = d.pendingDecision as SelectCardsDecision
        d.submitDecision(you, CardsSelectedResponse(decision.id, listOf(after!!)))

        d.state.getEntity(you)?.get<TheRingComponent>()?.temptCount shouldBe 1
    }
})
