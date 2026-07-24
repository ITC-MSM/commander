package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.NineLivesFamiliar
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Nine-Lives Familiar (FDN #66) — {1}{B}{B} 1/1 Creature — Cat.
 *
 * "This creature enters with eight revival counters on it if you cast it.
 *  When this creature dies, if it had a revival counter on it, return it to the battlefield with
 *  one fewer revival counter on it at the beginning of the next end step."
 *
 * Covers the cast-gated enters-with-counters replacement, the last-known-counter intervening "if"
 * on the dies trigger, and the delayed end-step return with the decremented count.
 */
class NineLivesFamiliarScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(NineLivesFamiliar)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun revivalCounters(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()?.counters?.get(CounterType.REVIVAL) ?: 0

    /** Cast the Familiar from hand and resolve it. Returns its entity id. */
    fun castFamiliar(driver: GameTestDriver, player: EntityId): EntityId {
        val cat = driver.putCardInHand(player, "Nine-Lives Familiar")
        driver.giveMana(player, Color.BLACK, 3)
        driver.castSpell(player, cat).isSuccess shouldBe true
        driver.bothPass()
        return cat
    }

    /** Bolt [victim] and let the death (and its dies trigger) resolve fully. */
    fun bolt(driver: GameTestDriver, player: EntityId, victim: EntityId) {
        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, bolt, listOf(victim)).isSuccess shouldBe true
        driver.bothPass() // Bolt resolves; the creature dies and its trigger goes on the stack
        driver.bothPass() // the dies trigger resolves, scheduling the delayed return
    }

    test("cast Familiar enters with eight revival counters") {
        val driver = newDriver()
        val player = driver.player1

        val cat = castFamiliar(driver, player)

        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(cat) shouldBe true
        revivalCounters(driver, cat) shouldBe 8
    }

    test("a Familiar put onto the battlefield without being cast enters with no counters") {
        val driver = newDriver()
        val player = driver.player1

        val cat = driver.putCreatureOnBattlefield(player, "Nine-Lives Familiar")

        revivalCounters(driver, cat) shouldBe 0
    }

    test("dies trigger returns it at the next end step with one fewer revival counter") {
        val driver = newDriver()
        val player = driver.player1

        val cat = castFamiliar(driver, player)
        bolt(driver, player, cat)

        // It is dead and stays dead until the end step — the return is delayed, not immediate.
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(cat) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(cat) shouldBe false

        driver.passPriorityUntil(Step.END)
        driver.bothPass() // the delayed trigger resolves

        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(cat) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(cat) shouldBe false
        revivalCounters(driver, cat) shouldBe 7
    }

    test("dying during the end step defers the return to the next turn's end step") {
        val driver = newDriver()
        val player = driver.player1

        val cat = castFamiliar(driver, player)
        driver.passPriorityUntil(Step.END)
        bolt(driver, player, cat)

        // The current end step's beginning has already passed, so nothing comes back now.
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(cat) shouldBe true

        // Leave this turn, then reach the *next* end step (the opponent's — the oracle says "the
        // next end step", not "your next end step").
        driver.passPriorityUntil(Step.UPKEEP)
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(cat) shouldBe true
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(cat) shouldBe true
        revivalCounters(driver, cat) shouldBe 7
    }

    test("a Familiar that entered without revival counters stays in the graveyard") {
        val driver = newDriver()
        val player = driver.player1

        val cat = driver.putCreatureOnBattlefield(player, "Nine-Lives Familiar")
        bolt(driver, player, cat)

        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        // The intervening "if" was false, so nothing was ever scheduled.
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(cat) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(cat) shouldBe false
    }
})
