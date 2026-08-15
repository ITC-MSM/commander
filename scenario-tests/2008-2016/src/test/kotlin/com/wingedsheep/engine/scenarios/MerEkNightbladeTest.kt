package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.cards.MerEkNightblade
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Mer-Ek Nightblade:
 * {3}{B} Creature — Orc Assassin 2/3
 * Outlast {B}
 * Each creature you control with a +1/+1 counter on it has deathtouch.
 */
class MerEkNightbladeTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MerEkNightblade))
        return driver
    }

    fun addCounters(driver: GameTestDriver, entityId: EntityId, type: CounterType, count: Int) {
        val newState = driver.state.updateEntity(entityId) { container ->
            val existing = container.get<CountersComponent>() ?: CountersComponent()
            container.with(existing.withAdded(type, count))
        }
        driver.replaceState(newState)
    }

    test("creature with +1/+1 counter gains deathtouch from Mer-Ek Nightblade") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val nightblade = driver.putCreatureOnBattlefield(activePlayer, "Mer-Ek Nightblade")
        val bears = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")

        // Bears without counter should not have deathtouch
        val projected1 = projector.project(driver.state)
        projected1.hasKeyword(bears, Keyword.DEATHTOUCH) shouldBe false

        // Add a +1/+1 counter to bears
        addCounters(driver, bears, CounterType.PLUS_ONE_PLUS_ONE, 1)

        // Bears with counter should now have deathtouch
        val projected2 = projector.project(driver.state)
        projected2.hasKeyword(bears, Keyword.DEATHTOUCH) shouldBe true
    }

    test("Mer-Ek Nightblade itself gains deathtouch when it has a +1/+1 counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val nightblade = driver.putCreatureOnBattlefield(activePlayer, "Mer-Ek Nightblade")

        // Nightblade without counter should not have deathtouch
        val projected1 = projector.project(driver.state)
        projected1.hasKeyword(nightblade, Keyword.DEATHTOUCH) shouldBe false

        // Add a +1/+1 counter
        addCounters(driver, nightblade, CounterType.PLUS_ONE_PLUS_ONE, 1)

        // Nightblade with counter should have deathtouch
        val projected2 = projector.project(driver.state)
        projected2.hasKeyword(nightblade, Keyword.DEATHTOUCH) shouldBe true
    }

    test("opponent creatures with +1/+1 counters do not gain deathtouch") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(activePlayer, "Mer-Ek Nightblade")
        val opponentCreature = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        // Add a +1/+1 counter to opponent's creature
        addCounters(driver, opponentCreature, CounterType.PLUS_ONE_PLUS_ONE, 1)

        // Opponent's creature should NOT have deathtouch
        val projected = projector.project(driver.state)
        projected.hasKeyword(opponentCreature, Keyword.DEATHTOUCH) shouldBe false
    }
})
