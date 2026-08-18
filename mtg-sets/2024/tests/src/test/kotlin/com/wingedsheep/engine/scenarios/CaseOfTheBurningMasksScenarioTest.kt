package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheBurningMasks
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Case of the Burning Masks — {1}{R}{R} Enchantment — Case.
 *
 * "Three or more sources you controlled dealt damage this turn" counts *objects*, which is the new
 * engine tracker. Two rulings define it and both are tested here: several creatures each count, and
 * one source that deals damage twice counts once. The Solved ability's shape — exile three, but
 * only the chosen one becomes playable — is the other thing worth pinning.
 */
class CaseOfTheBurningMasksScenarioTest : FunSpec({

    /** {R} instant: 1 damage to target creature. Each copy cast is its own source. */
    val testSpark = card("Test Spark") {
        manaCost = "{R}"
        typeLine = "Instant"
        oracleText = "Test Spark deals 1 damage to target creature."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature))
            effect = Effects.DealDamage(1, t)
        }
    }

    /** {R} instant that deals damage *twice* — two damage events from one source. */
    val testDoubleSpark = card("Test Double Spark") {
        manaCost = "{R}"
        typeLine = "Instant"
        oracleText = "Test Double Spark deals 1 damage to target creature, then 1 damage to it again."
        spell {
            val t = target("target", TargetCreature(filter = TargetFilter.Creature))
            effect = Effects.Composite(
                Effects.DealDamage(1, t),
                Effects.DealDamage(1, t)
            )
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaseOfTheBurningMasks)
        driver.registerCard(testSpark)
        driver.registerCard(testDoubleSpark)
        driver.initMirrorMatch(Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    fun GameTestDriver.cast(name: String, target: EntityId) {
        val spell = putCardInHand(player1, name)
        giveMana(player1, Color.RED, 1)
        castSpell(player1, spell, listOf(target)).isSuccess shouldBe true
        bothPass()
    }

    test("one source dealing damage twice counts once — two sources leave it unsolved") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Burning Masks")
        val wall = driver.putCreatureOnBattlefield(driver.player2, "Force of Nature") // 5/5

        driver.cast("Test Double Spark", wall) // one source, two damage events
        driver.cast("Test Spark", wall) // a second source

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false
    }

    test("three separate sources solve it (CR 719.3a)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Burning Masks")
        val wall = driver.putCreatureOnBattlefield(driver.player2, "Force of Nature") // 5/5

        driver.cast("Test Spark", wall)
        driver.cast("Test Spark", wall)
        driver.cast("Test Spark", wall)

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
    }

    test("the enters trigger deals 3 damage and is itself one of the three sources") {
        val driver = newDriver()
        val victim = driver.putCreatureOnBattlefield(driver.player2, "Centaur Courser") // 3/3

        val card = driver.putCardInHand(driver.player1, "Case of the Burning Masks")
        driver.giveMana(driver.player1, Color.RED, 3)
        driver.castSpell(driver.player1, card).isSuccess shouldBe true
        driver.bothPass()
        driver.submitTargetSelection(driver.player1, listOf(victim))
        driver.bothPass()

        driver.state.getBattlefield().contains(victim) shouldBe false

        // The Case counts as a source; two more make three.
        val wall = driver.putCreatureOnBattlefield(driver.player2, "Force of Nature")
        driver.cast("Test Spark", wall)
        driver.cast("Test Spark", wall)

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(card) shouldBe true
    }

    test("Solved — exiles three cards and only the chosen one may be played") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Burning Masks")
        val wall = driver.putCreatureOnBattlefield(driver.player2, "Force of Nature")

        driver.cast("Test Spark", wall)
        driver.cast("Test Spark", wall)
        driver.cast("Test Spark", wall)
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val exileBefore = driver.getExile(driver.player1).size
        val ability = CaseOfTheBurningMasks.activatedAbilities.first().id
        driver.submitSuccess(ActivateAbility(driver.player1, case, ability))
        driver.bothPass()

        // The choice: pick the first of the three exiled cards.
        val exiled = driver.getExile(driver.player1).drop(exileBefore)
        exiled.size shouldBe 3
        driver.submitCardSelection(driver.player1, listOf(exiled.first()))

        val playable = driver.legalActions(driver.player1).filter { it.sourceZone == "EXILE" }
        playable.size shouldBe 1
        driver.state.getZone(com.wingedsheep.engine.state.ZoneKey(driver.player1, Zone.EXILE))
            .containsAll(exiled) shouldBe true
    }
})
