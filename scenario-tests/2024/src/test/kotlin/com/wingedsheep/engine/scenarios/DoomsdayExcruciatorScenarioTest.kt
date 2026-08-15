package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.DoomsdayExcruciator
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

/**
 * Doomsday Excruciator (DSK #94) — {B}{B}{B}{B}{B}{B} Creature — Demon 6/6, Flying.
 *
 *   "When this creature enters, if it was cast, each player exiles all but the bottom six cards of
 *    their library face down."
 *   "At the beginning of your upkeep, draw a card."
 *
 * Composition: the ETB is an EntersBattlefield trigger gated by WasCast; the body is a
 * ForEachPlayer(Player.Each) that gathers each player's top `librarySize - 6` cards (clamped to
 * zero) and moves them to exile face down (FaceDownMode.HIDDEN). The upkeep payoff is a standard
 * YourUpkeep draw.
 */
class DoomsdayExcruciatorScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + DoomsdayExcruciator)
        initMirrorMatch(deck = Deck.of("Swamp" to 60), startingLife = 20)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("when cast, each player exiles all but the bottom six cards of their library face down") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        val myLibBefore = d.state.getLibrary(me).size
        val oppLibBefore = d.state.getLibrary(opp).size
        myLibBefore shouldBeGreaterThan 6
        oppLibBefore shouldBeGreaterThan 6

        // Cast Doomsday Excruciator for real (so "if it was cast" holds).
        val demon = d.putCardInHand(me, "Doomsday Excruciator")
        d.giveMana(me, Color.BLACK, 6)
        d.castSpell(me, demon).error shouldBe null
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Each player's library is reduced to exactly the bottom six cards.
        d.state.getLibrary(me).size shouldBe 6
        d.state.getLibrary(opp).size shouldBe 6

        // The exiled cards are face down in exile, and the count matches what was removed.
        val myExile = d.getExile(me)
        val oppExile = d.getExile(opp)
        myExile.size shouldBe (myLibBefore - 6)
        oppExile.size shouldBe (oppLibBefore - 6)
        myExile.forEach { d.state.getEntity(it)?.get<FaceDownComponent>() shouldBe FaceDownComponent }
        oppExile.forEach { d.state.getEntity(it)?.get<FaceDownComponent>() shouldBe FaceDownComponent }
    }

    test("a player with six or fewer cards in library exiles nothing") {
        val d = driver()
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        // Trim the opponent's library down to exactly four cards.
        val keep = d.state.getLibrary(opp).take(4)
        d.replaceState(
            d.state.copy(
                zones = d.state.zones + (com.wingedsheep.engine.state.ZoneKey(opp, Zone.LIBRARY) to keep)
            )
        )
        d.state.getLibrary(opp).size shouldBe 4

        val demon = d.putCardInHand(me, "Doomsday Excruciator")
        d.giveMana(me, Color.BLACK, 6)
        d.castSpell(me, demon).error shouldBe null
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // Opponent had <= 6, so nothing is exiled from their library (CR / ruling).
        d.state.getLibrary(opp).size shouldBe 4
        d.getExile(opp).size shouldBe 0
    }

    test("draws a card at the beginning of your upkeep") {
        val d = driver()
        val me = d.activePlayer!!

        d.putCreatureOnBattlefield(me, "Doomsday Excruciator")

        // Advance past the opponent's turn to my next turn's upkeep (the YourUpkeep trigger only
        // fires on the controller's own upkeep). Turn 1 = mine (PRECOMBAT_MAIN now); step through
        // the opponent's upkeep + main, then stop at my next upkeep.
        d.passPriorityUntil(Step.UPKEEP, maxPasses = 300) // opponent's upkeep (turn 2)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 300) // opponent's main
        // Record the hand size after the opponent's turn so the assertion isolates my upkeep draw.
        d.passPriorityUntil(Step.UPKEEP, maxPasses = 300) // my upkeep (turn 3)
        d.activePlayer shouldBe me
        val handBefore = d.getHandSize(me)
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.getHandSize(me) shouldBe (handBefore + 1)
    }
})
