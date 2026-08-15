package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Prime Speaker Zegana (GTC #188).
 *
 * {2}{G}{G}{U}{U} Legendary Creature — Merfolk Wizard 1/1
 *  Zegana enters with X +1/+1 counters on it, where X is the greatest power among other
 *   creatures you control.
 *  When Zegana enters, draw cards equal to its power.
 *
 * Pins the entry replacement (`excludeSelf` keeps her own 1 power out of the max) and the draw
 * trigger reading the projected power that already includes those counters.
 */
class PrimeSpeakerZeganaScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Cast and fully resolve Zegana (spell + the ETB draw trigger). Returns her entity id. */
    fun castZegana(driver: GameTestDriver, player: EntityId): EntityId {
        val zegana = driver.putCardInHand(player, "Prime Speaker Zegana")
        driver.giveMana(player, Color.GREEN, 4)
        driver.giveMana(player, Color.BLUE, 2)
        val cast = driver.castSpell(player, zegana)
        withClue("Zegana should cast: ${cast.error}") { cast.error shouldBe null }
        driver.bothPass() // resolve the spell — she enters with counters
        driver.bothPass() // resolve the ETB draw trigger
        return zegana
    }

    fun plusOneCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("X is the greatest power among OTHER creatures you control, and she draws that many + her base") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        driver.putCreatureOnBattlefield(player, "Charging Rhino") // 4/4 — the greatest power
        driver.putCreatureOnBattlefield(player, "Grizzly Bears")  // 2/2

        val handBefore = driver.getHandSize(player)
        val zegana = castZegana(driver, player)

        withClue("greatest other power is 4, so she enters with four +1/+1 counters") {
            plusOneCounters(driver, zegana) shouldBe 4
        }
        val projected = projector.project(driver.state)
        projected.getPower(zegana) shouldBe 5
        projected.getToughness(zegana) shouldBe 5
        withClue("draw cards equal to her power (5); Zegana was added to and then cast out of hand") {
            driver.getHandSize(player) shouldBe handBefore + 5
        }
    }

    test("with no other creatures X is 0 — she is a 1/1 and draws one card") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val handBefore = driver.getHandSize(player)
        val zegana = castZegana(driver, player)

        plusOneCounters(driver, zegana) shouldBe 0
        val projected = projector.project(driver.state)
        projected.getPower(zegana) shouldBe 1
        projected.getToughness(zegana) shouldBe 1
        driver.getHandSize(player) shouldBe handBefore + 1
    }
})
