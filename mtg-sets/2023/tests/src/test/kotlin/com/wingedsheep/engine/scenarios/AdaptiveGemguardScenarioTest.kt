package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.AdaptiveGemguard
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Adaptive Gemguard (LCI #3).
 *
 * Adaptive Gemguard {3}{W}
 * Artifact Creature — Gnome 3/3
 * Tap two untapped artifacts and/or creatures you control: Put a +1/+1 counter on this creature.
 * Activate only as a sorcery.
 *
 * Covers:
 *  - Happy path: two untapped creatures available → activation taps both and adds a +1/+1 counter.
 *  - Sorcery-speed gate: activation is rejected outside a main phase with an empty stack.
 *  - Cost gate: activation is rejected when fewer than two matching untapped permanents are available.
 */
class AdaptiveGemguardScenarioTest : FunSpec({

    val abilityId = AdaptiveGemguard.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AdaptiveGemguard))
        return driver
    }

    fun counterCount(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("tapping two creatures puts a +1/+1 counter on Adaptive Gemguard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gemguard = driver.putCreatureOnBattlefield(activePlayer, "Adaptive Gemguard")
        driver.removeSummoningSickness(gemguard)
        val bear1 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear1)
        val bear2 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear2)

        counterCount(driver, gemguard) shouldBe 0

        driver.submitSuccess(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = gemguard,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = listOf(bear1, bear2))
            )
        )
        driver.bothPass() // resolve the ability

        // Both permanents tapped to pay the cost.
        driver.isTapped(bear1) shouldBe true
        driver.isTapped(bear2) shouldBe true

        // Gemguard received a +1/+1 counter.
        counterCount(driver, gemguard) shouldBe 1
    }

    test("cannot be activated at instant speed (sorcery-speed gate)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        val gemguard = driver.putCreatureOnBattlefield(activePlayer, "Adaptive Gemguard")
        driver.removeSummoningSickness(gemguard)
        val bear1 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear1)
        val bear2 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear2)

        // Advance into the declare-attackers step — not a main phase with an empty stack.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        driver.submitExpectFailure(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = gemguard,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = listOf(bear1, bear2))
            )
        )
    }

    test("cannot be activated without two matching untapped permanents") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gemguard = driver.putCreatureOnBattlefield(activePlayer, "Adaptive Gemguard")
        driver.removeSummoningSickness(gemguard)
        // Only one other creature — cost requires two.
        val bear1 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear1)

        driver.submitExpectFailure(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = gemguard,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = listOf(bear1))
            )
        )
    }
})
