package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FoundFootage
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Found Footage (DSK #246) — {1} Artifact — Clue.
 *
 * "{2}, Sacrifice this artifact: Surveil 2, then draw a card."
 *
 * Exercises the activated ability: surveil 2 (keeping both cards on top), then draw. We verify the
 * Clue is sacrificed (moves to the graveyard) and the controller's hand grows by exactly one card
 * (the draw — surveil itself does not change hand size when nothing is binned).
 */
class FoundFootageScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + FoundFootage)
        initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20, skipMulligans = true)
    }

    test("activated ability sacrifices the Clue, surveils 2, then draws a card") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val footage = d.putPermanentOnBattlefield(you, "Found Footage")
        d.giveColorlessMana(you, 2)

        val handBefore = d.getHand(you).size

        d.submit(ActivateAbility(you, footage, FoundFootage.activatedAbilities[0].id)).isSuccess shouldBe true
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Surveil 2 pauses to choose which looked-at cards to bin — keep both on top.
        while (d.isPaused) {
            when (val decision = d.pendingDecision) {
                is SelectCardsDecision -> d.submitDecision(you, CardsSelectedResponse(decision.id, emptyList()))
                is ReorderLibraryDecision -> d.submitDecision(you, OrderedResponse(decision.id, decision.cards))
                else -> break
            }
        }
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // The Clue was sacrificed to the graveyard.
        d.getGraveyard(you) shouldContain footage

        // Hand grew by exactly one (the draw); surveil kept both cards on top so binned nothing.
        d.getHand(you).size shouldBe handBefore + 1
    }
})
