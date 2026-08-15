package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.ClockworkAvian
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.Step
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Clockwork Avian's refill ability (ATQ #45).
 *
 * "{X}, {T}: Put up to X +1/+0 counters on this creature. This ability can't cause the total number
 *  of +1/+0 counters on this creature to be greater than four. Activate only during your upkeep."
 *
 * The cap clause is modeled as putting `min(X, 4 - current)` counters (floored at 0), and the ability
 * is restricted to your upkeep. These tests exercise the cap formula and the timing restriction —
 * the part of the card the end-of-combat-shed tests in [ClockworkAvianScenarioTest] don't cover.
 */
class ClockworkAvianActivatedAbilityScenarioTest : FunSpec({

    val refillAbilityId = ClockworkAvian.activatedAbilities[0].id // {X}, {T}: put up to X +1/+0 counters

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        return driver
    }

    fun plusOneZero(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ZERO) ?: 0

    test("refill puts the full X when it stays at or below four total") {
        val driver = createDriver()
        val player = driver.player1
        val avian = driver.putCreatureOnBattlefield(player, "Clockwork Avian")
        driver.removeSummoningSickness(avian)
        // Below the cap: one counter, so X=2 fits entirely (1 + 2 = 3 ≤ 4).
        driver.addComponent(avian, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ZERO to 1)))

        driver.passPriorityUntil(Step.UPKEEP)
        driver.giveColorlessMana(player, 2)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = avian, abilityId = refillAbilityId, xValue = 2)
        ).isSuccess shouldBe true
        driver.bothPass()

        plusOneZero(driver, avian) shouldBe 3 // 1 + min(2, 4-1) = 1 + 2
    }

    test("refill caps the placement so the total never exceeds four (min(X, 4 - current))") {
        val driver = createDriver()
        val player = driver.player1
        val avian = driver.putCreatureOnBattlefield(player, "Clockwork Avian")
        driver.removeSummoningSickness(avian)
        // Two counters already; X=5 would overshoot, so only 4-2 = 2 may be added.
        driver.addComponent(avian, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ZERO to 2)))

        driver.passPriorityUntil(Step.UPKEEP)
        driver.giveColorlessMana(player, 5)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = avian, abilityId = refillAbilityId, xValue = 5)
        ).isSuccess shouldBe true
        driver.bothPass()

        plusOneZero(driver, avian) shouldBe 4 // capped at four total, NOT 2 + 5 = 7
    }

    test("refill cannot be activated outside your upkeep") {
        val driver = createDriver()
        val player = driver.player1
        val avian = driver.putCreatureOnBattlefield(player, "Clockwork Avian")
        driver.removeSummoningSickness(avian)
        driver.addComponent(avian, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ZERO to 1)))

        // In the main phase (not upkeep) the DuringStep(UPKEEP) restriction makes activation illegal.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveColorlessMana(player, 2)
        driver.submitExpectFailure(
            ActivateAbility(playerId = player, sourceId = avian, abilityId = refillAbilityId, xValue = 2)
        )

        plusOneZero(driver, avian) shouldBe 1 // unchanged — the ability never resolved
    }
})
