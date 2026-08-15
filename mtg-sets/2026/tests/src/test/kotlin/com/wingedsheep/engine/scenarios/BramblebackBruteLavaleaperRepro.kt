package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ecl.cards.BramblebackBrute
import com.wingedsheep.mtg.sets.definitions.ecl.cards.Lavaleaper
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.basicLand
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test for the interaction between Lavaleaper and Brambleback Brute's activated
 * ability. Before the fix, activating Brambleback Brute's {1}{R}, Remove a counter ability
 * with Lavaleaper on the battlefield failed with "Cannot pay mana cost": the auto-tap path
 * dropped the Lavaleaper bonus that the mana solver had spent on the generic part of the
 * cost, so the floating pool was short by that bonus when payAbilityCost ran.
 */
class BramblebackBruteLavaleaperRepro : FunSpec({

    val TestMountain = basicLand("Mountain") {}
    val TestForest = basicLand("Forest") {}

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(TestMountain, TestForest, Lavaleaper, BramblebackBrute))
        return driver
    }

    fun addMinusCounters(driver: GameTestDriver, entityId: EntityId, n: Int) {
        val container = driver.state.getEntity(entityId)!!
        val current = container.get<CountersComponent>() ?: CountersComponent()
        val updated = container.with(current.withAdded(CounterType.MINUS_ONE_MINUS_ONE, n))
        driver.replaceState(driver.state.withEntity(entityId, updated))
    }

    test("Brambleback Brute can activate its ability with Lavaleaper in play") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Forest" to 20), startingLife = 20)
        val p = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(p, "Lavaleaper")
        val bb = driver.putCreatureOnBattlefield(p, "Brambleback Brute")
        driver.removeSummoningSickness(bb)
        addMinusCounters(driver, bb, 2)
        repeat(2) { driver.putLandOnBattlefield(p, "Mountain") }

        val abilityId = BramblebackBrute.script.activatedAbilities.first().id
        val target = ChosenTarget.Permanent(bb)

        val result = driver.submit(
            ActivateAbility(playerId = p, sourceId = bb, abilityId = abilityId, targets = listOf(target))
        )
        result.isSuccess shouldBe true
    }
})
