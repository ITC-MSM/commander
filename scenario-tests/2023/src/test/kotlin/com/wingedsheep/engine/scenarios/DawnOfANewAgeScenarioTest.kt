package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.DawnOfANewAge
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Dawn of a New Age (LTR).
 *
 * Dawn of a New Age — {1}{W}, Enchantment
 * This enchantment enters with a hope counter on it for each creature you control.
 * At the beginning of your end step, remove a hope counter from this enchantment.
 * If you do, draw a card. Then if this enchantment has no hope counters on it,
 * sacrifice it and you gain 4 life.
 *
 * Tests cover:
 *  - ETB-with-counters: count = number of creatures you control at the moment Dawn enters.
 *  - End-step drain: remove a hope counter and draw a card while counters remain.
 *  - End-step terminal: removing the last counter triggers the sac + 4 life.
 *  - Edge case: no creatures controlled at ETB → 0 hope counters → first end step
 *    fires no remove (and therefore no draw), but still sacrifices and gains 4 life
 *    because the "Then if … no hope counters" clause re-evaluates.
 */
class DawnOfANewAgeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DawnOfANewAge))
        return driver
    }

    fun hopeCount(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.HOPE) ?: 0

    test("enters with one hope counter per creature you control") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put three creatures under player1's control before Dawn enters.
        driver.putCreatureOnBattlefield(player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(player1, "Grizzly Bears")
        // An opponent creature must NOT count toward Dawn's ETB counters.
        driver.putCreatureOnBattlefield(player2, "Grizzly Bears")

        val dawnCard = driver.putCardInHand(player1, "Dawn of a New Age")
        driver.giveMana(player1, Color.WHITE, 1)
        driver.giveColorlessMana(player1, 1)
        driver.castSpell(player1, dawnCard)
        driver.passPriority(player1)
        driver.passPriority(player2)

        hopeCount(driver, dawnCard) shouldBe 3
    }

    test("end step removes one hope counter and draws a card while counters remain") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(player1, "Grizzly Bears")

        val dawnCard = driver.putCardInHand(player1, "Dawn of a New Age")
        driver.giveMana(player1, Color.WHITE, 1)
        driver.giveColorlessMana(player1, 1)
        driver.castSpell(player1, dawnCard)
        driver.passPriority(player1)
        driver.passPriority(player2)

        hopeCount(driver, dawnCard) shouldBe 2

        val handBefore = driver.getHandSize(player1)
        val lifeBefore = driver.getLifeTotal(player1)

        // Advance to player1's end step and resolve the trigger.
        driver.passPriorityUntil(Step.END)
        driver.passPriority(player1)
        driver.passPriority(player2)

        hopeCount(driver, dawnCard) shouldBe 1
        driver.getHandSize(player1) shouldBe handBefore + 1
        driver.getLifeTotal(player1) shouldBe lifeBefore // not yet zero — no sac
        // Dawn should still be on the battlefield (not in graveyard).
        driver.state.getBattlefield().contains(dawnCard) shouldBe true
    }

    test("removing the last hope counter draws and then sacrifices for 4 life") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Exactly one creature → Dawn enters with exactly one hope counter.
        driver.putCreatureOnBattlefield(player1, "Grizzly Bears")

        val dawnCard = driver.putCardInHand(player1, "Dawn of a New Age")
        driver.giveMana(player1, Color.WHITE, 1)
        driver.giveColorlessMana(player1, 1)
        driver.castSpell(player1, dawnCard)
        driver.passPriority(player1)
        driver.passPriority(player2)
        hopeCount(driver, dawnCard) shouldBe 1

        val handBefore = driver.getHandSize(player1)
        val lifeBefore = driver.getLifeTotal(player1)

        driver.passPriorityUntil(Step.END)
        driver.passPriority(player1)
        driver.passPriority(player2)

        // Removed the last counter → drew a card AND sacrificed AND gained 4 life.
        driver.getHandSize(player1) shouldBe handBefore + 1
        driver.getLifeTotal(player1) shouldBe lifeBefore + 4
        driver.state.getBattlefield().contains(dawnCard) shouldBe false
        driver.state.getGraveyard(player1).contains(dawnCard) shouldBe true
    }

    test("ETB with zero creatures controlled: no draw, but still sacrifices and gains 4 life") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Grizzly Bears" to 20),
            skipMulligans = true
        )

        val player1 = driver.activePlayer!!
        val player2 = driver.getOpponent(player1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Zero creatures under our control → Dawn enters with 0 hope counters.
        val dawnCard = driver.putCardInHand(player1, "Dawn of a New Age")
        driver.giveMana(player1, Color.WHITE, 1)
        driver.giveColorlessMana(player1, 1)
        driver.castSpell(player1, dawnCard)
        driver.passPriority(player1)
        driver.passPriority(player2)
        hopeCount(driver, dawnCard) shouldBe 0

        val handBefore = driver.getHandSize(player1)
        val lifeBefore = driver.getLifeTotal(player1)

        driver.passPriorityUntil(Step.END)
        driver.passPriority(player1)
        driver.passPriority(player2)

        // No counter to remove → no draw. But the "Then if … no hope counters" clause fires.
        driver.getHandSize(player1) shouldBe handBefore
        driver.getLifeTotal(player1) shouldBe lifeBefore + 4
        driver.state.getBattlefield().contains(dawnCard) shouldBe false
        driver.state.getGraveyard(player1).contains(dawnCard) shouldBe true
    }
})
