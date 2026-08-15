package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SeymourFlux
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Seymour Flux (FIN #558) — {4}{B} Legendary Creature 5/5.
 *
 *   At the beginning of your upkeep, you may pay 1 life. If you do, draw a card and put a
 *   +1/+1 counter on Seymour Flux.
 *
 * Exercises the optional pay-then-payoff: the [com.wingedsheep.sdk.scripting.effects.Gate.MayPay]
 * gate pays 1 life, the [then] branch draws and adds a counter; declining does nothing.
 */
class SeymourFluxScenarioTest : FunSpec({

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun newGame(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SeymourFlux))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    // From my turn's main phase, advance to my *next* upkeep (skips the opponent's turn).
    fun advanceToMyNextUpkeep(driver: GameTestDriver) {
        driver.passPriorityUntil(Step.UPKEEP)         // opponent's upkeep (no trigger)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN) // opponent's main
        driver.passPriorityUntil(Step.UPKEEP)         // my next upkeep — Seymour triggers
    }

    test("paying 1 life draws a card and puts a +1/+1 counter on Seymour") {
        val (driver, you) = newGame()
        val seymour = driver.putCreatureOnBattlefield(you, "Seymour Flux")

        advanceToMyNextUpkeep(driver)
        val lifeBefore = driver.getLifeTotal(you)
        val handBefore = driver.getHandSize(you)

        // Resolve the upkeep trigger -> "you may pay 1 life" decision.
        driver.bothPass()
        driver.submitYesNo(you, true)
        var guard = 0
        while (guard++ < 10 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()

        driver.getLifeTotal(you) shouldBe lifeBefore - 1
        driver.getHandSize(you) shouldBe handBefore + 1
        plusCounters(driver, seymour) shouldBe 1
    }

    test("declining pays no life, draws nothing, and adds no counter") {
        val (driver, you) = newGame()
        val seymour = driver.putCreatureOnBattlefield(you, "Seymour Flux")

        advanceToMyNextUpkeep(driver)
        val lifeBefore = driver.getLifeTotal(you)
        val handBefore = driver.getHandSize(you)

        driver.bothPass()
        driver.submitYesNo(you, false)

        driver.getLifeTotal(you) shouldBe lifeBefore
        driver.getHandSize(you) shouldBe handBefore
        plusCounters(driver, seymour) shouldBe 0
    }
})
