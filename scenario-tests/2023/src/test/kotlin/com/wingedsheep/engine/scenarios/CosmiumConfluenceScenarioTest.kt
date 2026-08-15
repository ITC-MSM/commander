package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.CosmiumConfluence
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Cosmium Confluence (LCI #181) — {4}{G} Sorcery.
 *
 * "Choose three. You may choose the same mode more than once.
 * • Search your library for a Cave card, put it onto the battlefield tapped, then shuffle.
 * • Put three +1/+1 counters on a Cave you control. It becomes a 0/0 Elemental creature
 *   with haste. It's still a land.
 * • Destroy target enchantment."
 *
 * Covered:
 *  1. Choosing the same mode more than once (allowRepeat = true): mode 2 (destroy enchantment)
 *     chosen twice and mode 1 (animate Cave) once — two enchantments are destroyed and one
 *     Cave receives 3 +1/+1 counters and becomes a 0/0 Elemental creature permanently.
 *
 *  2. Mixed selection with repeated mode 1: mode 1 chosen twice (two different Caves, each
 *     receiving 3 +1/+1 counters and becoming animated) and mode 2 once (destroys one
 *     enchantment). Confirms the Confluence processes each mode-slot independently.
 */
class CosmiumConfluenceScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CosmiumConfluence)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Count +1/+1 counters on a permanent. */
    fun plusOneCounters(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)
            ?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    /** Drain the stack without interactive decisions (no library searches or modal re-choices). */
    fun GameTestDriver.drainStack(maxIterations: Int = 20) {
        var guard = 0
        while (state.stack.isNotEmpty() && guard++ < maxIterations) {
            bothPass()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Test 1: Choose mode 2 twice and mode 1 once.
    //         Verifies allowRepeat by using mode 2 (destroy enchantment) two times.
    // ─────────────────────────────────────────────────────────────────────────────
    test("choosing destroy enchantment twice and animate Cave once destroys both enchantments and animates Cave") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // One Cave you control (target for mode 1).
        val cave = driver.putPermanentOnBattlefield(me, "Captivating Cave")

        // Two enchantments for opponent (targets for the two mode 2 slots).
        val ench1 = driver.putPermanentOnBattlefield(opp, "Test Enchantment")
        val ench2 = driver.putPermanentOnBattlefield(opp, "Test Enchantment")

        val spell = driver.putCardInHand(me, "Cosmium Confluence")
        // {4}{G}: 4 colorless + 1 green.
        driver.giveColorlessMana(me, 4)
        driver.giveMana(me, Color.GREEN, 1)

        // chosenModes = [2, 2, 1]: destroy ench1, destroy ench2, animate cave.
        val cast = driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(
                    ChosenTarget.Permanent(ench1),
                    ChosenTarget.Permanent(ench2),
                    ChosenTarget.Permanent(cave)
                ),
                chosenModes = listOf(2, 2, 1),
                modeTargetsOrdered = listOf(
                    listOf(ChosenTarget.Permanent(ench1)),
                    listOf(ChosenTarget.Permanent(ench2)),
                    listOf(ChosenTarget.Permanent(cave))
                )
            )
        )
        cast.isSuccess shouldBe true

        driver.drainStack()

        // Both enchantments should be destroyed.
        driver.findPermanent(opp, "Test Enchantment") shouldBe null

        // Cave should still be on the battlefield (permanent animation, not exiled/destroyed).
        driver.findPermanent(me, "Captivating Cave") shouldNotBe null

        // Mode 1 placed exactly 3 +1/+1 counters on the Cave.
        plusOneCounters(driver, cave) shouldBe 3

        // Mode 1 animated the Cave to a 0/0 Elemental creature (permanent BecomeCreature).
        driver.state.projectedState.isCreature(cave) shouldBe true
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Test 2: Choose mode 1 twice and mode 2 once.
    //         Two different Caves are animated; one enchantment is destroyed.
    // ─────────────────────────────────────────────────────────────────────────────
    test("choosing animate Cave twice and destroy enchantment once animates both Caves and destroys enchantment") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Two Caves you control (each targeted separately by the two mode 1 slots).
        val cave1 = driver.putPermanentOnBattlefield(me, "Captivating Cave")
        val cave2 = driver.putPermanentOnBattlefield(me, "Captivating Cave")

        // One enchantment for mode 2.
        val ench = driver.putPermanentOnBattlefield(opp, "Test Enchantment")

        val spell = driver.putCardInHand(me, "Cosmium Confluence")
        driver.giveColorlessMana(me, 4)
        driver.giveMana(me, Color.GREEN, 1)

        // chosenModes = [1, 1, 2]: animate cave1, animate cave2, destroy ench.
        val cast = driver.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(
                    ChosenTarget.Permanent(cave1),
                    ChosenTarget.Permanent(cave2),
                    ChosenTarget.Permanent(ench)
                ),
                chosenModes = listOf(1, 1, 2),
                modeTargetsOrdered = listOf(
                    listOf(ChosenTarget.Permanent(cave1)),
                    listOf(ChosenTarget.Permanent(cave2)),
                    listOf(ChosenTarget.Permanent(ench))
                )
            )
        )
        cast.isSuccess shouldBe true

        driver.drainStack()

        // Enchantment should be destroyed.
        driver.findPermanent(opp, "Test Enchantment") shouldBe null

        // Each Cave should have exactly 3 +1/+1 counters from its mode 1 slot.
        plusOneCounters(driver, cave1) shouldBe 3
        plusOneCounters(driver, cave2) shouldBe 3

        // Both Caves should be animated into creatures.
        driver.state.projectedState.isCreature(cave1) shouldBe true
        driver.state.projectedState.isCreature(cave2) shouldBe true
    }
})
