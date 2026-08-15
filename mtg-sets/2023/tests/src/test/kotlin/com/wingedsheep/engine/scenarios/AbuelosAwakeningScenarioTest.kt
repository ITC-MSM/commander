package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Abuelo's Awakening (LCI #1).
 *
 * {X}{3}{W} Sorcery
 * "Return target artifact or non-Aura enchantment card from your graveyard to the battlefield with
 *  X additional +1/+1 counters on it. It's a 1/1 Spirit creature with flying in addition to its
 *  other types."
 *
 * Exercises the reanimate-with-X-counters + permanent BecomeCreature composite:
 *  - the targeted noncreature card leaves the graveyard for the battlefield,
 *  - it permanently becomes a 1/1 Spirit creature with flying **in addition to** its printed types
 *    (artifact/enchantment kept, CREATURE + Spirit + flying added),
 *  - X +1/+1 counters are placed, so final P/T = (1 + X)/(1 + X).
 */
class AbuelosAwakeningScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun plusOneCounters(driver: GameTestDriver, id: EntityId): Int =
        driver.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("reanimates a noncreature artifact as a flying Spirit with X +1/+1 counters, keeping its type") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        // A noncreature artifact in the graveyard, and the spell in hand.
        val boulder = driver.putCardInGraveyard(player, "Runaway Boulder")
        val spell = driver.putCardInHand(player, "Abuelo's Awakening")
        // {X=2}{3}{W} = 6 mana total (>=1 white). White pays the generic and the {W} pip.
        driver.giveMana(player, Color.WHITE, 6)

        val cast = driver.submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Card(boulder, player, Zone.GRAVEYARD)),
                xValue = 2,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        cast.isSuccess shouldBe true
        driver.bothPass()

        // Moved from graveyard to the battlefield.
        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(boulder) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(boulder) shouldBe false

        val projected = projector.project(driver.state)
        // 1/1 base + two +1/+1 counters = 3/3.
        plusOneCounters(driver, boulder) shouldBe 2
        projected.getPower(boulder) shouldBe 3
        projected.getToughness(boulder) shouldBe 3
        // Became a creature with flying, in addition to its printed artifact type.
        projected.hasType(boulder, "CREATURE") shouldBe true
        projected.hasType(boulder, "ARTIFACT") shouldBe true
        projected.hasSubtype(boulder, "Spirit") shouldBe true
        projected.hasKeyword(boulder, Keyword.FLYING) shouldBe true
    }

    test("reanimates a non-Aura enchantment as a 1/1 flying Spirit with X=0, keeping its enchantment type") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val enchantment = driver.putCardInGraveyard(player, "Test Enchantment")
        val spell = driver.putCardInHand(player, "Abuelo's Awakening")
        // {X=0}{3}{W} = 4 mana total.
        driver.giveMana(player, Color.WHITE, 4)

        val cast = driver.submit(
            CastSpell(
                playerId = player,
                cardId = spell,
                targets = listOf(ChosenTarget.Card(enchantment, player, Zone.GRAVEYARD)),
                xValue = 0,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        cast.isSuccess shouldBe true
        driver.bothPass()

        driver.state.getZone(ZoneKey(player, Zone.BATTLEFIELD)).contains(enchantment) shouldBe true
        driver.state.getZone(ZoneKey(player, Zone.GRAVEYARD)).contains(enchantment) shouldBe false

        val projected = projector.project(driver.state)
        // No counters with X=0 → a plain 1/1.
        plusOneCounters(driver, enchantment) shouldBe 0
        projected.getPower(enchantment) shouldBe 1
        projected.getToughness(enchantment) shouldBe 1
        // Creature + Spirit + flying added; enchantment type retained.
        projected.hasType(enchantment, "CREATURE") shouldBe true
        projected.hasType(enchantment, "ENCHANTMENT") shouldBe true
        projected.hasSubtype(enchantment, "Spirit") shouldBe true
        projected.hasKeyword(enchantment, Keyword.FLYING) shouldBe true
    }
})
