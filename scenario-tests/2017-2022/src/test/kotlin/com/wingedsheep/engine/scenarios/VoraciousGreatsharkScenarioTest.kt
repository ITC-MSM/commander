package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.iko.cards.VoraciousGreatshark
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Voracious Greatshark (IKO #70) — {3}{U}{U} Shark 5/4, Flash.
 *
 *   "When this creature enters, counter target artifact or creature spell."
 *
 * Flash lets the Shark be cast in response to a creature spell; its ETB trigger then goes on the
 * stack above the still-unresolved spell and counters it. Verifies the creature spell is countered
 * (moves to its controller's graveyard) and the Shark stays on the battlefield.
 */
class VoraciousGreatsharkScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + VoraciousGreatshark)
        initMirrorMatch(deck = Deck.of("Island" to 60), startingLife = 20)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("counters a creature spell cast by the opponent") {
        val d = driver()
        // The active player casts a creature at sorcery speed on their own main phase; the
        // responder flashes the Shark in response to counter it.
        val caster = d.activePlayer!!
        val responder = d.getOpponent(caster)

        val oppCreature = d.putCardInHand(caster, "Grizzly Bears")
        d.giveMana(caster, Color.GREEN, 2)
        d.castSpell(caster, oppCreature).error shouldBe null
        d.passPriority(caster)

        // Responder flashes in Voracious Greatshark in response.
        val shark = d.putCardInHand(responder, "Voracious Greatshark")
        d.giveMana(responder, Color.BLUE, 5)
        d.castSpell(responder, shark).error shouldBe null

        // Resolve the Shark; its ETB pauses to choose the spell to counter.
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()
        (d.pendingDecision as ChooseTargetsDecision)
        d.submitTargetSelection(responder, listOf(oppCreature))

        // Resolve the ETB counter.
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.getGraveyard(caster) shouldContain oppCreature
        d.getPermanents(responder) shouldContain shark
    }
})
