package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Vincent Valentine // Galian Beast (FIN #125).
 *
 * Vincent Valentine — {2}{B}{B} Legendary Creature — Assassin 2/2:
 *   Whenever a creature an opponent controls dies, put a number of +1/+1 counters on Vincent
 *   Valentine equal to that creature's power.
 *   Whenever Vincent Valentine attacks, you may transform it.
 *
 * Galian Beast — Legendary Creature — Werewolf Beast 3/2:
 *   Trample, lifelink
 *   When Galian Beast dies, return it to the battlefield tapped (front face up).
 *
 * Exercises three independently-proven primitives composed on one card: the
 * `EntityProperty(Triggering, Power)` last-known-information read on an opponent's-creature-dies
 * trigger (proven elsewhere by Jackal, Genius Geneticist / Doran, Besieged by Time), the
 * attack-triggered `MayEffect(TransformEffect)` (proven by Cecil, Dark Knight / Grub, Storied
 * Matriarch), and a DFC's "dies, return tapped" trigger relying on the automatic front-face-up
 * re-entry default (proven by Unstoppable Slasher's plain "return to the battlefield tapped").
 */
class VincentValentineScenarioTest : FunSpec({

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
    }

    fun plusCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun newGame(): Pair<GameTestDriver, EntityId> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver to driver.activePlayer!!
    }

    fun GameTestDriver.boltDeath(caster: EntityId, victim: EntityId) {
        val bolt = putCardInHand(caster, "Lightning Bolt")
        giveMana(caster, Color.RED, 1)
        castSpell(caster, bolt, listOf(victim)).isSuccess shouldBe true
        resolveStack(this) // bolt resolves, victim dies, dies-trigger goes on the stack
        resolveStack(this) // dies trigger resolves
    }

    test("an opponent's creature dying puts +1/+1 counters on Vincent equal to its power") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        val vincent = driver.putCreatureOnBattlefield(you, "Vincent Valentine")
        val hillGiant = driver.putCreatureOnBattlefield(opponent, "Hill Giant") // 3/3

        driver.boltDeath(you, hillGiant)

        driver.state.getBattlefield().contains(hillGiant) shouldBe false
        plusCounters(driver, vincent) shouldBe 3
    }

    test("Vincent's own creature dying does not put counters on Vincent") {
        val (driver, you) = newGame()
        val vincent = driver.putCreatureOnBattlefield(you, "Vincent Valentine")
        val hillGiant = driver.putCreatureOnBattlefield(you, "Hill Giant") // 3/3, same controller

        driver.boltDeath(you, hillGiant)

        driver.state.getBattlefield().contains(hillGiant) shouldBe false
        plusCounters(driver, vincent) shouldBe 0
    }

    test("attacking offers a may-transform; declining leaves Vincent on the front face") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        val vincent = driver.putCreatureOnBattlefield(you, "Vincent Valentine")
        driver.removeSummoningSickness(vincent)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(vincent to opponent)).isSuccess shouldBe true
        resolveStack(driver) // attack trigger resolves into the may-transform yes/no prompt
        driver.isPaused shouldBe true
        driver.submitYesNo(you, false)

        driver.state.getEntity(vincent)!!.get<DoubleFacedComponent>()!!.currentFace shouldBe
            DoubleFacedComponent.Face.FRONT
        driver.state.getEntity(vincent)!!.get<CardComponent>()!!.name shouldBe "Vincent Valentine"
    }

    test("accepting the may-transform turns Vincent into Galian Beast") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        val vincent = driver.putCreatureOnBattlefield(you, "Vincent Valentine")
        driver.removeSummoningSickness(vincent)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(vincent to opponent)).isSuccess shouldBe true
        resolveStack(driver)
        driver.isPaused shouldBe true
        driver.submitYesNo(you, true)

        val container = driver.state.getEntity(vincent)
        container.shouldNotBeNull()
        container.get<CardComponent>()!!.name shouldBe "Galian Beast"
        container.get<DoubleFacedComponent>()!!.currentFace shouldBe DoubleFacedComponent.Face.BACK
    }

    test("Galian Beast dying returns it to the battlefield tapped, front face up, as Vincent Valentine") {
        val (driver, you) = newGame()
        val opponent = driver.state.turnOrder.first { it != you }
        val vincent = driver.putCreatureOnBattlefield(you, "Vincent Valentine")
        driver.removeSummoningSickness(vincent)

        // Transform Vincent into Galian Beast via the attack trigger.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(vincent to opponent)).isSuccess shouldBe true
        resolveStack(driver)
        driver.submitYesNo(you, true)
        driver.state.getEntity(vincent)!!.get<CardComponent>()!!.name shouldBe "Galian Beast"

        // Kill Galian Beast (3/2) with a 3-damage bolt — lethal. Active player passes priority
        // first so the (non-active) opponent gets a window to cast at instant speed.
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.passPriority(you)
        driver.castSpell(opponent, bolt, listOf(vincent)).isSuccess shouldBe true
        resolveStack(driver) // bolt resolves, Galian Beast dies, dies-trigger goes on the stack
        resolveStack(driver) // dies trigger: return it to the battlefield tapped

        // Same entity id, now back on the battlefield as the front face.
        driver.state.getBattlefield().contains(vincent) shouldBe true
        val container = driver.state.getEntity(vincent)
        container.shouldNotBeNull()
        container.get<CardComponent>()!!.name shouldBe "Vincent Valentine"
        container.get<DoubleFacedComponent>()!!.currentFace shouldBe DoubleFacedComponent.Face.FRONT
        container.has<TappedComponent>() shouldBe true
    }
})
