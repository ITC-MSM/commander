package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Sahagin (FIN #71).
 *
 * Sahagin {1}{U} Creature — Merfolk Warrior 1/3
 * Whenever you cast a noncreature spell, if at least four mana was spent to cast it, put a
 * +1/+1 counter on this creature and it can't be blocked this turn.
 *
 * Exercises the new [com.wingedsheep.sdk.dsl.Conditions.TriggeringSpellManaSpentAtLeast]
 * intervening-if: a 4-mana noncreature spell triggers the payoff; a 1-mana one does not.
 */
class SahaginScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("casting a 4-mana noncreature spell adds a +1/+1 counter and makes Sahagin unblockable") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val sahagin = driver.putCreatureOnBattlefield(active, "Sahagin")

        // Cast Stoke the Flames ({2}{R}{R}, a 4-mana instant) targeting the opponent, paying full mana.
        val stoke = driver.putCardInHand(active, "Stoke the Flames")
        driver.giveMana(active, Color.RED, 2)
        driver.giveColorlessMana(active, 2)
        driver.castSpellWithTargets(
            active,
            stoke,
            listOf(entityIdToChosenTarget(driver.state, opponent)),
        ).isSuccess shouldBe true
        // Resolve Sahagin's trigger (on top of the stack), then the spell.
        driver.bothPass()
        driver.bothPass()

        plusCounters(driver, sahagin) shouldBe 1
        projector.project(driver.state).hasKeyword(sahagin, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
    }

    test("casting a sub-4-mana noncreature spell does nothing") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val sahagin = driver.putCreatureOnBattlefield(active, "Sahagin")

        // Lightning Bolt ({R}) is a 1-mana noncreature spell — below the four-mana threshold.
        val bolt = driver.putCardInHand(active, "Lightning Bolt")
        driver.giveMana(active, Color.RED, 1)
        driver.castSpellWithTargets(
            active,
            bolt,
            listOf(entityIdToChosenTarget(driver.state, opponent)),
        ).isSuccess shouldBe true
        driver.bothPass()
        driver.bothPass()

        plusCounters(driver, sahagin) shouldBe 0
        projector.project(driver.state).hasKeyword(sahagin, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
    }
})
