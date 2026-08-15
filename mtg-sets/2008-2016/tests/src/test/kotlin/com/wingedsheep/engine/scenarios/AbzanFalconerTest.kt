package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.cards.AbzanFalconer
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Abzan Falconer:
 * {2}{W} Creature — Human Soldier 2/3
 * Outlast {W}
 * Each creature you control with a +1/+1 counter on it has flying.
 */
class AbzanFalconerTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AbzanFalconer))
        return driver
    }

    fun addCounters(driver: GameTestDriver, entityId: EntityId, type: CounterType, count: Int) {
        val newState = driver.state.updateEntity(entityId) { container ->
            val existing = container.get<CountersComponent>() ?: CountersComponent()
            container.with(existing.withAdded(type, count))
        }
        driver.replaceState(newState)
    }

    test("creature with +1/+1 counter gains flying from Abzan Falconer") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val falconer = driver.putCreatureOnBattlefield(activePlayer, "Abzan Falconer")
        val bears = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")

        // Bears without counter should not have flying
        val projected1 = projector.project(driver.state)
        projected1.hasKeyword(bears, Keyword.FLYING) shouldBe false

        // Add a +1/+1 counter to bears
        addCounters(driver, bears, CounterType.PLUS_ONE_PLUS_ONE, 1)

        // Bears with counter should now have flying
        val projected2 = projector.project(driver.state)
        projected2.hasKeyword(bears, Keyword.FLYING) shouldBe true
    }

    test("Abzan Falconer itself gains flying when it has a +1/+1 counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val falconer = driver.putCreatureOnBattlefield(activePlayer, "Abzan Falconer")

        // Falconer without counter should not have flying
        val projected1 = projector.project(driver.state)
        projected1.hasKeyword(falconer, Keyword.FLYING) shouldBe false

        // Add a +1/+1 counter
        addCounters(driver, falconer, CounterType.PLUS_ONE_PLUS_ONE, 1)

        // Falconer with counter should have flying
        val projected2 = projector.project(driver.state)
        projected2.hasKeyword(falconer, Keyword.FLYING) shouldBe true
    }

    test("opponent creatures with +1/+1 counters do not gain flying") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(activePlayer, "Abzan Falconer")
        val opponentCreature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Add a +1/+1 counter to opponent's creature
        addCounters(driver, opponentCreature, CounterType.PLUS_ONE_PLUS_ONE, 1)

        // Opponent's creature should NOT have flying
        val projected = projector.project(driver.state)
        projected.hasKeyword(opponentCreature, Keyword.FLYING) shouldBe false
    }
})
