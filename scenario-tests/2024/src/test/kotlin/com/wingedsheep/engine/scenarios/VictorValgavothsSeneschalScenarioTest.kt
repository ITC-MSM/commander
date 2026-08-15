package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Victor, Valgavoth's Seneschal (DSK) — the escalating Eerie ability.
 *
 * "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room, surveil 2
 * if this is the first time this ability has resolved this turn. If it's the second time, each
 * opponent discards a card. If it's the third time, put a creature card from a graveyard onto the
 * battlefield under your control."
 *
 * Drives the enchantment-enters half three times in one turn (casting three Test Enchantments) and
 * verifies the per-turn resolution counter escalates the payoff: 1st = surveil, 2nd = opponent
 * discards, 3rd = reanimate from any graveyard under your control. The counter lives on the source
 * permanent, so both Eerie triggers would share it (only the enchantment half is exercised here).
 */
class VictorValgavothsSeneschalScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Plains" to 20, "Swamp" to 20), startingLife = 20)
    }

    fun GameTestDriver.castEnchantmentAndResolveToDecision(you: EntityId) {
        // A prior Eerie resolution that routed a decision to the opponent can leave priority with
        // them; hand it back to the active player (passing with an empty stack just returns priority,
        // it doesn't end the step) so the next sorcery-speed cast is legal.
        var p = 0
        while (state.priorityPlayerId != null && state.priorityPlayerId != you && !isPaused && p++ < 4) {
            passPriority(state.priorityPlayerId!!)
        }
        val ench = putCardInHand(you, "Test Enchantment") // {1}{W}
        giveMana(you, Color.WHITE, 2)
        castSpell(you, ench).isSuccess shouldBe true
        // Resolve the enchantment spell, then its on-enter Eerie trigger, until something pauses.
        var guard = 0
        while (!isPaused && state.stack.isNotEmpty() && guard++ < 20) bothPass()
    }

    test("Eerie escalates: 1st surveils, 2nd makes each opponent discard, 3rd reanimates") {
        val d = driver()
        val you = d.activePlayer!!
        val opp = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(you, "Victor, Valgavoth's Seneschal")
        // Two creatures across both graveyards ("a graveyard") so the 3rd-time reanimation is a real
        // choice (ChooseExactly(1) of one candidate would auto-resolve without a decision).
        val corpse = d.putCardInGraveyard(opp, "Centaur Courser")
        d.putCardInGraveyard(you, "Black Creature")

        // --- 1st resolution: surveil 2 (controller looks at the top, chooses up to 2 → graveyard). ---
        d.castEnchantmentAndResolveToDecision(you)
        withClue("First Eerie resolution surveils — a min-0 card selection for the controller") {
            d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            val surveil = d.pendingDecision as SelectCardsDecision
            surveil.playerId shouldBe you
            surveil.minSelections shouldBe 0
        }
        d.submitCardSelection(you, emptyList()) // keep both on top
        if (d.pendingDecision is ReorderLibraryDecision) {
            d.submitOrderedResponse(you, (d.pendingDecision as ReorderLibraryDecision).cards)
        }
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // --- 2nd resolution: each opponent discards a card. ---
        val oppHandBefore = d.getHandSize(opp)
        d.castEnchantmentAndResolveToDecision(you)
        withClue("Second Eerie resolution asks the opponent to discard") {
            d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            (d.pendingDecision as SelectCardsDecision).playerId shouldBe opp
        }
        val discard = d.pendingDecision as SelectCardsDecision
        d.submitCardSelection(opp, discard.options.take(1))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()
        withClue("The opponent discarded exactly one card") {
            d.getHandSize(opp) shouldBe oppHandBefore - 1
        }

        // --- 3rd resolution: put a creature card from a graveyard onto the battlefield (your control). ---
        d.castEnchantmentAndResolveToDecision(you)
        withClue("Third Eerie resolution asks the controller to choose a creature to reanimate") {
            d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            val reanimate = d.pendingDecision as SelectCardsDecision
            reanimate.playerId shouldBe you
            reanimate.options.contains(corpse) shouldBe true
        }
        d.submitCardSelection(you, listOf(corpse))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        withClue("The opponent's creature is reanimated onto the battlefield under YOUR control") {
            d.getController(corpse) shouldBe you
        }
    }
})
