package com.wingedsheep.engine.scenarios
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.player.CreaturesDiedThisTurnComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tdm.cards.BarrensteppeSiege
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Barrensteppe Siege's two modes.
 *
 * Abzan: at your end step, +1/+1 counter on each creature you control.
 * Mardu: at your end step, if a creature died under your control this turn, each opponent
 *        sacrifices a creature of their choice (intervening-if scoped to the controller via the
 *        new ControlledCreatureDiedThisTurn condition).
 */
class BarrensteppeSiegeTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BarrensteppeSiege))
        return driver
    }

    test("Abzan mode puts a +1/+1 counter on each creature you control at end step") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val bear1 = driver.putCreatureOnBattlefield(you, "Centaur Courser")
        val bear2 = driver.putCreatureOnBattlefield(you, "Centaur Courser")

        val siege = driver.putPermanentOnBattlefield(you, "Barrensteppe Siege")
        driver.addComponent(siege, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("abzan"))))

        driver.passPriorityUntil(Step.END)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.state.getEntity(bear1)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
        driver.state.getEntity(bear2)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
    }

    test("Mardu mode: each opponent sacrifices a creature when a creature died under your control") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        // The opponent has a single creature, so the edict auto-sacrifices it.
        driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        val siege = driver.putPermanentOnBattlefield(you, "Barrensteppe Siege")
        driver.addComponent(siege, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("mardu"))))
        // Simulate a creature having died under your control this turn (engine-maintained tracker).
        driver.addComponent(you, CreaturesDiedThisTurnComponent(1))

        driver.passPriorityUntil(Step.END)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getCreatures(opponent).size shouldBe 0
    }

    test("Mardu mode does nothing when no creature died under your control this turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        val siege = driver.putPermanentOnBattlefield(you, "Barrensteppe Siege")
        driver.addComponent(siege, CastChoicesComponent(chosen = mapOf(ChoiceSlot.MODE to ChoiceValue.TextChoice("mardu"))))
        // No CreaturesDiedThisTurnComponent on `you` — the intervening-if fails, ability never fires.

        driver.passPriorityUntil(Step.END)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getCreatures(opponent).size shouldBe 1
    }
})
