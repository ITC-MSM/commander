package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Skullsnap Nuisance — {U}{B} Creature — Insect Skeleton, 1/4, Flying
 *   "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 *    surveil 1."
 *
 * The Eerie ability is two triggered abilities (enchantment-you-control enters; Room fully
 * unlocked), both performing `Patterns.Library.surveil(1)`. This exercises the enchantment-enters
 * half: casting your own enchantment fires the surveil, and an opponent's enchantment entering
 * does not (the trigger is "an enchantment you control").
 */
class SkullsnapNuisanceScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Island" to 20, "Swamp" to 20), startingLife = 20)
    }

    test("an enchantment you control entering surveils 1") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(you, "Skullsnap Nuisance")
        val top = d.putCardOnTopOfLibrary(you, "Island")

        val enchantment = d.putCardInHand(you, "Test Enchantment") // {1}{W}
        d.giveMana(you, Color.WHITE, 2)
        d.castSpell(you, enchantment)
        // Resolve the enchantment, then its on-enter Eerie trigger, until the surveil pauses.
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Surveil 1 pauses for the keep/graveyard choice — proof the Eerie trigger fired.
        d.isPaused shouldBe true
        d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        val select = d.pendingDecision as SelectCardsDecision
        select.options.size shouldBe 1

        // Put the looked-at card into the graveyard.
        d.submitDecision(you, CardsSelectedResponse(decisionId = select.id, selectedCards = listOf(top)))
        if (d.isPaused && d.pendingDecision is ReorderLibraryDecision) {
            val reorder = d.pendingDecision as ReorderLibraryDecision
            d.submitOrderedResponse(you, reorder.cards)
        }
        d.getGraveyard(you).contains(top) shouldBe true
    }

    test("an opponent's enchantment entering does NOT surveil") {
        val d = driver()
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(you, "Skullsnap Nuisance")

        // Hand the opponent priority by advancing to their turn, then cast their enchantment.
        d.passPriorityUntil(Step.END)
        d.bothPass()
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val enchantment = d.putCardInHand(opponent, "Test Enchantment")
        d.giveMana(opponent, Color.WHITE, 2)
        d.castSpell(opponent, enchantment)
        d.bothPass()

        // No surveil decision — the enchantment isn't controlled by Skullsnap's controller.
        d.isPaused shouldBe false
    }
})
