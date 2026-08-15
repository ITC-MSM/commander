package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SephirothPlanetsHeir
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Sephiroth, Planet's Heir (FIN #553) — {4}{U}{B} Legendary Creature 4/4, Vigilance.
 *
 *   When Sephiroth enters, creatures your opponents control get -2/-2 until end of turn.
 *   Whenever a creature an opponent controls dies, put a +1/+1 counter on Sephiroth.
 *
 * Exercises the enters group debuff (a per-creature -2/-2 floating effect over opponents'
 * creatures) and the opponent-creature-dies counter trigger — including the case where the
 * debuff itself kills a creature and feeds the trigger.
 */
class SephirothPlanetsHeirScenarioTest : FunSpec({

    val projector = StateProjector()

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun newGame(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SephirothPlanetsHeir))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    fun GameTestDriver.castSephiroth(you: EntityId): EntityId {
        val card = putCardInHand(you, "Sephiroth, Planet's Heir")
        giveMana(you, Color.BLUE, 1)
        giveMana(you, Color.BLACK, 1)
        giveColorlessMana(you, 4)
        castSpell(you, card).isSuccess shouldBe true
        bothPass()
        return card
    }

    test("enters gives opponents' creatures -2/-2 until end of turn") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        val giant = driver.putCreatureOnBattlefield(opponent, "Hill Giant") // 3/3

        driver.castSephiroth(you)
        resolveStack(driver)

        val projected = projector.project(driver.state)
        projected.getPower(giant) shouldBe 1
        projected.getToughness(giant) shouldBe 1
    }

    test("the enters debuff kills a 2/2 and that death puts a +1/+1 counter on Sephiroth") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        val bears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears") // 2/2

        val sephiroth = driver.castSephiroth(you)
        resolveStack(driver)

        // 2/2 with -2/-2 becomes 0/0 and is put into the graveyard as a state-based action.
        driver.state.getBattlefield().contains(bears) shouldBe false
        // That death is a creature an opponent controls dying -> +1/+1 on Sephiroth.
        plusCounters(driver, sephiroth) shouldBe 1
    }

    test("a surviving opponent creature does not add a counter") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        driver.putCreatureOnBattlefield(opponent, "Hill Giant") // 3/3 -> 1/1, survives

        val sephiroth = driver.castSephiroth(you)
        resolveStack(driver)

        plusCounters(driver, sephiroth) shouldBe 0
    }
})
