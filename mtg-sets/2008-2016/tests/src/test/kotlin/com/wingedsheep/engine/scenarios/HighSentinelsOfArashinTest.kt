package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.cards.HighSentinelsOfArashin
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for High Sentinels of Arashin:
 * {3}{W} Creature — Bird Soldier 3/4
 * Flying
 * High Sentinels of Arashin gets +1/+1 for each other creature you control with a +1/+1 counter on it.
 * {3}{W}: Put a +1/+1 counter on target creature.
 */
class HighSentinelsOfArashinTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(HighSentinelsOfArashin))
        return driver
    }

    fun addCounters(driver: GameTestDriver, entityId: EntityId, type: CounterType, count: Int) {
        val newState = driver.state.updateEntity(entityId) { container ->
            val existing = container.get<CountersComponent>() ?: CountersComponent()
            container.with(existing.withAdded(type, count))
        }
        driver.replaceState(newState)
    }

    test("base stats are 3/4 with flying and no other creatures with counters") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val sentinels = driver.putCreatureOnBattlefield(activePlayer, "High Sentinels of Arashin")

        val projected = projector.project(driver.state)
        projected.getPower(sentinels) shouldBe 3
        projected.getToughness(sentinels) shouldBe 4
        projected.hasKeyword(sentinels, Keyword.FLYING) shouldBe true
    }

    test("gets +1/+1 for each other creature you control with a +1/+1 counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val sentinels = driver.putCreatureOnBattlefield(activePlayer, "High Sentinels of Arashin")
        val bears1 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        val bears2 = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")

        // No counters yet - base 3/4
        val projected1 = projector.project(driver.state)
        projected1.getPower(sentinels) shouldBe 3
        projected1.getToughness(sentinels) shouldBe 4

        // Add counter to one creature - should be 4/5
        addCounters(driver, bears1, CounterType.PLUS_ONE_PLUS_ONE, 1)
        val projected2 = projector.project(driver.state)
        projected2.getPower(sentinels) shouldBe 4
        projected2.getToughness(sentinels) shouldBe 5

        // Add counter to second creature - should be 5/6
        addCounters(driver, bears2, CounterType.PLUS_ONE_PLUS_ONE, 1)
        val projected3 = projector.project(driver.state)
        projected3.getPower(sentinels) shouldBe 5
        projected3.getToughness(sentinels) shouldBe 6
    }

    test("does not count itself even with a +1/+1 counter for the dynamic bonus") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val sentinels = driver.putCreatureOnBattlefield(activePlayer, "High Sentinels of Arashin")

        // Add counter to sentinels itself - should be 4/5 (base 3/4 + counter 1/1, no dynamic bonus)
        addCounters(driver, sentinels, CounterType.PLUS_ONE_PLUS_ONE, 1)
        val projected = projector.project(driver.state)
        projected.getPower(sentinels) shouldBe 4
        projected.getToughness(sentinels) shouldBe 5
    }

    test("does not count opponent creatures with +1/+1 counters") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val sentinels = driver.putCreatureOnBattlefield(activePlayer, "High Sentinels of Arashin")
        val opponentCreature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        addCounters(driver, opponentCreature, CounterType.PLUS_ONE_PLUS_ONE, 1)

        val projected = projector.project(driver.state)
        projected.getPower(sentinels) shouldBe 3
        projected.getToughness(sentinels) shouldBe 4
    }

    test("activated ability puts +1/+1 counter on target creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val sentinels = driver.putCreatureOnBattlefield(activePlayer, "High Sentinels of Arashin")
        val bears = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")

        // Provide mana for the ability ({3}{W})
        repeat(4) { driver.putLandOnBattlefield(activePlayer, "Plains") }

        val abilityId = HighSentinelsOfArashin.activatedAbilities.first().id
        driver.submitSuccess(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = sentinels,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(bears))
            )
        )
        driver.bothPass()

        val counters = driver.state.getEntity(bears)?.get<CountersComponent>()
        counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1

        // Now sentinels should be boosted too (bears has a counter)
        val projected = projector.project(driver.state)
        projected.getPower(sentinels) shouldBe 4
        projected.getToughness(sentinels) shouldBe 5
    }
})
