package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lgn.cards.KilnmouthDragon
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Kilnmouth Dragon:
 * {5}{R}{R} Creature — Dragon 5/5
 * Amplify 3
 * Flying
 * {T}: Kilnmouth Dragon deals damage equal to the number of +1/+1 counters on it to any target.
 */
class KilnmouthDragonTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(KilnmouthDragon))
        return driver
    }

    fun addCounters(driver: GameTestDriver, entityId: EntityId, type: CounterType, count: Int) {
        val newState = driver.state.updateEntity(entityId) { container ->
            val existing = container.get<CountersComponent>() ?: CountersComponent()
            container.with(existing.withAdded(type, count))
        }
        driver.replaceState(newState)
    }

    test("tap ability deals damage equal to +1/+1 counters on it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val dragon = driver.putCreatureOnBattlefield(activePlayer, "Kilnmouth Dragon")
        driver.removeSummoningSickness(dragon)

        // Add +1/+1 counters to simulate amplify (3 counters per reveal, say 2 reveals = 6)
        addCounters(driver, dragon, CounterType.PLUS_ONE_PLUS_ONE, 6)

        val lifeBefore = driver.getLifeTotal(opponent)

        val abilityId = KilnmouthDragon.activatedAbilities.first().id

        // Activate tap ability targeting opponent
        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = dragon,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opponent))
            )
        )
        result.isSuccess shouldBe true

        // Resolve
        driver.bothPass()

        // Should deal 6 damage (one per +1/+1 counter)
        driver.getLifeTotal(opponent) shouldBe lifeBefore - 6
    }

    test("tap ability deals zero damage with no counters") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val dragon = driver.putCreatureOnBattlefield(activePlayer, "Kilnmouth Dragon")
        driver.removeSummoningSickness(dragon)

        // No counters added
        val lifeBefore = driver.getLifeTotal(opponent)

        val abilityId = KilnmouthDragon.activatedAbilities.first().id

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = dragon,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opponent))
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass()

        driver.getLifeTotal(opponent) shouldBe lifeBefore
    }

    test("tap ability can target a creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val dragon = driver.putCreatureOnBattlefield(activePlayer, "Kilnmouth Dragon")
        driver.removeSummoningSickness(dragon)
        addCounters(driver, dragon, CounterType.PLUS_ONE_PLUS_ONE, 3)

        val bears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val abilityId = KilnmouthDragon.activatedAbilities.first().id

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = dragon,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(bears))
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass()

        // 3 damage to a 2/2 should kill it
        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
    }
})
