package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Tonberry (FIN #122).
 *
 * Tonberry {B} Creature — Salamander Horror 2/1
 * This creature enters tapped with a stun counter on it.
 * Chef's Knife — During your turn, this creature has first strike and deathtouch.
 */
class TonberryScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun stunCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0

    test("enters tapped with a stun counter; has first strike + deathtouch only on its controller's turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val tonberry = driver.putCardInHand(active, "Tonberry")
        driver.giveMana(active, Color.BLACK, 1)
        driver.castSpell(active, tonberry).isSuccess shouldBe true
        driver.bothPass() // resolve the creature; enters-replacements apply

        // Enters tapped with one stun counter.
        driver.isTapped(tonberry) shouldBe true
        stunCounters(driver, tonberry) shouldBe 1

        // On its controller's turn it has first strike and deathtouch.
        val onMyTurn = projector.project(driver.state)
        onMyTurn.hasKeyword(tonberry, Keyword.FIRST_STRIKE) shouldBe true
        onMyTurn.hasKeyword(tonberry, Keyword.DEATHTOUCH) shouldBe true

        // Advance into the opponent's turn — the conditional grants drop off.
        driver.passPriorityUntil(Step.END)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.activePlayer shouldBe driver.getOpponent(active)
        val onOpponentTurn = projector.project(driver.state)
        onOpponentTurn.hasKeyword(tonberry, Keyword.FIRST_STRIKE) shouldBe false
        onOpponentTurn.hasKeyword(tonberry, Keyword.DEATHTOUCH) shouldBe false
    }
})
