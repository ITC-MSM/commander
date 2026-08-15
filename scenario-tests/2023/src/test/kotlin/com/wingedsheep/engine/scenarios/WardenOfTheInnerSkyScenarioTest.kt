package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.WardenOfTheInnerSky
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Warden of the Inner Sky (LCI #43).
 *
 * Warden of the Inner Sky {W}
 * Creature — Human Soldier 1/2
 * As long as this creature has three or more counters on it, it has flying and vigilance.
 * Tap three untapped artifacts and/or creatures you control: Put a +1/+1 counter on this creature.
 * Scry 1. Activate only as a sorcery.
 *
 * Covers:
 *  - Happy path: tapping three creatures puts a +1/+1 counter on Warden (P/T grows) and scries 1.
 *  - Conditional static: FLYING + VIGILANCE are granted only once Warden has 3+ counters.
 *  - Counters of *any* kind count toward the threshold (stun + +1/+1 mixed), but only the
 *    +1/+1 counters contribute to P/T.
 *  - Sorcery-speed gate: activation is rejected outside a main phase with an empty stack.
 *  - Cost gate: activation is rejected without three matching untapped permanents.
 */
class WardenOfTheInnerSkyScenarioTest : FunSpec({

    val projector = StateProjector()
    val abilityId = WardenOfTheInnerSky.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(WardenOfTheInnerSky))
        return driver
    }

    fun counters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    // Activate once, tapping the three given permanents, then resolve the ability and any scry decision.
    fun activate(driver: GameTestDriver, warden: EntityId, activePlayer: EntityId, toTap: List<EntityId>) {
        driver.submitSuccess(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = warden,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = toTap),
            ),
        )
        driver.bothPass() // resolve the ability (adds the counter, then scries)
        // Scry 1 expands to a select-to-bottom decision AND a reorder-the-top decision
        // (MoveCollection with CardOrder.ControllerChooses); drain every pending decision so
        // the active player regains priority in the main phase before the next activation.
        while (driver.pendingDecision != null) driver.autoResolveDecision()
    }

    test("tapping three creatures adds a +1/+1 counter and scries") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val warden = driver.putCreatureOnBattlefield(activePlayer, "Warden of the Inner Sky")
        driver.removeSummoningSickness(warden)
        val bears = (1..3).map {
            val b = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
            driver.removeSummoningSickness(b)
            b
        }

        counters(driver, warden) shouldBe 0
        projector.getProjectedPower(driver.state, warden) shouldBe 1
        projector.getProjectedToughness(driver.state, warden) shouldBe 2

        activate(driver, warden, activePlayer, bears)

        bears.forEach { driver.isTapped(it) shouldBe true }
        counters(driver, warden) shouldBe 1
        projector.getProjectedPower(driver.state, warden) shouldBe 2
        projector.getProjectedToughness(driver.state, warden) shouldBe 3
    }

    test("gains flying and vigilance only once it has three or more counters") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val warden = driver.putCreatureOnBattlefield(activePlayer, "Warden of the Inner Sky")
        driver.removeSummoningSickness(warden)
        // Nine fodder creatures — three tapped per activation.
        val fodder = (1..9).map {
            val b = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
            driver.removeSummoningSickness(b)
            b
        }

        // No counters yet: neither keyword.
        projector.getProjectedKeywords(driver.state, warden).contains(Keyword.FLYING) shouldBe false
        projector.getProjectedKeywords(driver.state, warden).contains(Keyword.VIGILANCE) shouldBe false

        activate(driver, warden, activePlayer, fodder.subList(0, 3))
        activate(driver, warden, activePlayer, fodder.subList(3, 6))
        // Two counters — still below the threshold.
        counters(driver, warden) shouldBe 2
        projector.getProjectedKeywords(driver.state, warden).contains(Keyword.FLYING) shouldBe false
        projector.getProjectedKeywords(driver.state, warden).contains(Keyword.VIGILANCE) shouldBe false

        activate(driver, warden, activePlayer, fodder.subList(6, 9))
        // Third counter crosses the threshold: flying and vigilance apply.
        counters(driver, warden) shouldBe 3
        projector.getProjectedKeywords(driver.state, warden).contains(Keyword.FLYING) shouldBe true
        projector.getProjectedKeywords(driver.state, warden).contains(Keyword.VIGILANCE) shouldBe true
    }

    test("counters of any kind count toward the flying/vigilance threshold") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val warden = driver.putCreatureOnBattlefield(activePlayer, "Warden of the Inner Sky")
        // Two stun counters + one +1/+1 counter = three counters of mixed kinds.
        driver.addComponent(
            warden,
            CountersComponent(
                mapOf(CounterType.STUN to 2, CounterType.PLUS_ONE_PLUS_ONE to 1)
            )
        )

        projector.getProjectedKeywords(driver.state, warden).contains(Keyword.FLYING) shouldBe true
        projector.getProjectedKeywords(driver.state, warden).contains(Keyword.VIGILANCE) shouldBe true
        // Only the +1/+1 counter contributes to P/T: 1/2 base + 1/+1 = 2/3.
        projector.getProjectedPower(driver.state, warden) shouldBe 2
        projector.getProjectedToughness(driver.state, warden) shouldBe 3
    }

    test("cannot be activated at instant speed (sorcery-speed gate)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        val warden = driver.putCreatureOnBattlefield(activePlayer, "Warden of the Inner Sky")
        driver.removeSummoningSickness(warden)
        val bears = (1..3).map {
            val b = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
            driver.removeSummoningSickness(b)
            b
        }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        driver.submitExpectFailure(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = warden,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = bears),
            ),
        )
    }

    test("cannot be activated without three matching untapped permanents") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val warden = driver.putCreatureOnBattlefield(activePlayer, "Warden of the Inner Sky")
        driver.removeSummoningSickness(warden)
        val bear = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        driver.removeSummoningSickness(bear)
        // Only Warden + one bear are untapped — the cost needs three matching permanents.

        driver.submitExpectFailure(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = warden,
                abilityId = abilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = listOf(warden, bear)),
            ),
        )
    }
})
