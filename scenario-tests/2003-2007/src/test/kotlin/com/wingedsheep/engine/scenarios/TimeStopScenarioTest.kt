package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.chk.cards.TimeStop
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Engine coverage for Time Stop (CHK #97 — {4}{U}{U} instant, "End the turn.") over the
 * shared [com.wingedsheep.sdk.dsl.Effects.EndTheTurn] effect (CR 720).
 *
 * The card just wires an instant to `EndTheTurn`, so these tests pin the two consequences
 * players actually see: Time Stop exiles itself with the stack (CR 720.1a) and the turn
 * ends into the opponent's turn, and any other spell still on the stack is exiled unresolved
 * (CR 720.1a — "exile all spells and abilities, including this spell").
 */
class TimeStopScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + TimeStop)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        return driver
    }

    test("Time Stop exiles itself and ends the active player's turn") {
        val driver = createDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val timeStop = driver.putCardInHand(p1, "Time Stop")
        driver.giveMana(p1, Color.BLUE, 2)
        driver.giveColorlessMana(p1, 4)

        driver.castSpell(p1, timeStop)
        driver.bothPass() // resolve Time Stop -> end the turn

        // CR 720.1a: Time Stop is exiled with the stack, not put into the graveyard.
        driver.getExileCardNames(p1) shouldContain "Time Stop"
        driver.getGraveyardCardNames(p1) shouldNotContain "Time Stop"

        // The turn ended: it is now the opponent's turn.
        driver.activePlayer shouldBe p2
    }

    test("Time Stop exiles another spell on the stack, so it never resolves") {
        val driver = createDriver()
        val p1 = driver.player1

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A cheap creature spell to sit under Time Stop on the stack.
        val lions = driver.putCardInHand(p1, "Savannah Lions")
        val timeStop = driver.putCardInHand(p1, "Time Stop")
        driver.giveMana(p1, Color.WHITE, 1)
        driver.giveMana(p1, Color.BLUE, 2)
        driver.giveColorlessMana(p1, 4)

        // Stack (bottom -> top): Savannah Lions, Time Stop. The caster keeps priority
        // after each cast, so both go on the stack before anything resolves.
        driver.castSpell(p1, lions)
        driver.castSpell(p1, timeStop)
        driver.bothPass() // resolve Time Stop -> end the turn, exiling the rest of the stack

        // Savannah Lions was exiled with the stack — it never entered the battlefield.
        driver.findPermanent(p1, "Savannah Lions") shouldBe null
        driver.getExileCardNames(p1) shouldContain "Savannah Lions"
    }
})
