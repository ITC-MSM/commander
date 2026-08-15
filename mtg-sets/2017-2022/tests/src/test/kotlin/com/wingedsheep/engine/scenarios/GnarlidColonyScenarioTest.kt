package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.znr.cards.GnarlidColony
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Gnarlid Colony (ZNR) — {1}{G} Creature — Beast, 2/2, Kicker {2}{G}
 *
 * "Each creature you control with a +1/+1 counter on it has trample."
 *
 * Projection test: the trample-granting static reads +1/+1 counters in projected state.
 */
class GnarlidColonyScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(GnarlidColony)
        return driver
    }

    fun addPlusOneCounter(driver: GameTestDriver, id: EntityId) {
        driver.addComponent(id, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
    }

    test("grants trample only to creatures you control that have a +1/+1 counter") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30))
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gnarlid = driver.putCreatureOnBattlefield(you, "Gnarlid Colony")
        val ally = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val enemy = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        // Without any counters, nobody has trample (Gnarlid itself included — it needs a counter).
        var projected = projector.project(driver.state)
        projected.hasKeyword(gnarlid, Keyword.TRAMPLE) shouldBe false
        projected.hasKeyword(ally, Keyword.TRAMPLE) shouldBe false

        // Give the ally a +1/+1 counter → it gains trample.
        addPlusOneCounter(driver, ally)
        projected = projector.project(driver.state)
        projected.hasKeyword(ally, Keyword.TRAMPLE) shouldBe true

        // Give Gnarlid itself a counter → it grants trample to itself (excludeSelf = false).
        addPlusOneCounter(driver, gnarlid)
        projected = projector.project(driver.state)
        projected.hasKeyword(gnarlid, Keyword.TRAMPLE) shouldBe true

        // An opponent's creature with a +1/+1 counter is NOT affected ("you control").
        addPlusOneCounter(driver, enemy)
        projected = projector.project(driver.state)
        projected.hasKeyword(enemy, Keyword.TRAMPLE) shouldBe false
    }
})
