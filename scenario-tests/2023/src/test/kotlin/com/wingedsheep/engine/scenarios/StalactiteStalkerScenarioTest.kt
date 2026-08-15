package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.StalactiteStalker
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Stalactite Stalker (LCI #122): {B} 1/1 Goblin Rogue
 *
 * Menace
 * "At the beginning of your end step, if you descended this turn, put a +1/+1 counter on this creature."
 * "{2}{B}, Sacrifice this creature: Target creature gets -X/-X until end of turn, where X is this
 *  creature's power."
 *
 * Tests:
 * 1. Menace keyword is present.
 * 2. End-step descend trigger places a +1/+1 counter only when the controller descended this turn.
 * 3. The sacrifice ability gives the target -X/-X where X is the Stalker's last-known power (base 1).
 * 4. With a +1/+1 counter (power 2), X is 2 — proving last-known information reads the boosted power
 *    after the source has already left the battlefield.
 */
class StalactiteStalkerScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(StalactiteStalker))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
            startingPlayer = 0
        )
        return driver
    }

    fun GameTestDriver.descend(entityId: EntityId) {
        val result = ZoneTransitionService.moveToZone(
            state = state,
            entityId = entityId,
            destinationZone = Zone.GRAVEYARD
        )
        replaceState(result.state)
    }

    fun addCounters(driver: GameTestDriver, entityId: EntityId, type: CounterType, count: Int) {
        val newState = driver.state.updateEntity(entityId) { container ->
            val existing = container.get<CountersComponent>() ?: CountersComponent()
            container.with(existing.withAdded(type, count))
        }
        driver.replaceState(newState)
    }

    fun plusOneCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("Stalactite Stalker has menace") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val stalker = driver.putCreatureOnBattlefield(player, "Stalactite Stalker")

        val projected = projector.project(driver.state)
        projected.hasKeyword(stalker, com.wingedsheep.sdk.core.Keyword.MENACE) shouldBe true
    }

    test("no +1/+1 counter at end step when the controller has not descended") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val stalker = driver.putCreatureOnBattlefield(player, "Stalactite Stalker")

        val before = plusOneCounters(driver, stalker)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        plusOneCounters(driver, stalker) shouldBe before
    }

    test("a +1/+1 counter is placed at end step when the controller descended this turn") {
        val driver = createDriver()
        val player = driver.player1
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val stalker = driver.putCreatureOnBattlefield(player, "Stalactite Stalker")

        // Put a permanent card into the graveyard to satisfy CR 700.11 descend.
        val bears = driver.putCardInHand(player, "Grizzly Bears")
        driver.descend(bears)

        val before = plusOneCounters(driver, stalker)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        plusOneCounters(driver, stalker) shouldBe before + 1
    }

    test("sacrifice ability gives -1/-1 (X = base power 1) to target creature") {
        val driver = createDriver()
        val me = driver.player1
        val foe = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val stalker = driver.putCreatureOnBattlefield(me, "Stalactite Stalker") // 1/1
        val bears = driver.putCreatureOnBattlefield(foe, "Grizzly Bears") // 2/2

        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, Color.BLACK, 1)

        val abilityId = driver.cardRegistry.requireCard("Stalactite Stalker").activatedAbilities[0].id
        val result = driver.submit(
            ActivateAbility(
                playerId = me,
                sourceId = stalker,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(bears)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (result.error == null) shouldBe true

        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard < 20) {
            driver.bothPass()
            guard++
        }

        // Stalker paid its own sacrifice cost.
        driver.getGraveyard(me).any { driver.getCardName(it) == "Stalactite Stalker" } shouldBe true
        // 2/2 with -1/-1 is a 1/1 — still alive.
        val projected = projector.project(driver.state)
        projected.getPower(bears) shouldBe 1
        projected.getToughness(bears) shouldBe 1
    }

    test("sacrifice ability reads boosted last-known power (X = 2 with a +1/+1 counter)") {
        val driver = createDriver()
        val me = driver.player1
        val foe = driver.player2
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val stalker = driver.putCreatureOnBattlefield(me, "Stalactite Stalker") // 1/1
        addCounters(driver, stalker, CounterType.PLUS_ONE_PLUS_ONE, 1) // now 2/2
        val bears = driver.putCreatureOnBattlefield(foe, "Grizzly Bears") // 2/2

        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, Color.BLACK, 1)

        val abilityId = driver.cardRegistry.requireCard("Stalactite Stalker").activatedAbilities[0].id
        val result = driver.submit(
            ActivateAbility(
                playerId = me,
                sourceId = stalker,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(bears)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        (result.error == null) shouldBe true

        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard < 20) {
            driver.bothPass()
            guard++
        }

        // -2/-2 makes the 2/2 a 0/0 — it dies to a state-based action.
        driver.getGraveyard(foe).any { driver.getCardName(it) == "Grizzly Bears" } shouldBe true
    }
})
