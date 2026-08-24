package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase

/**
 * Tests for shadow evasion.
 *
 * CR 702.28b — "A creature with shadow can't be blocked by creatures without shadow, and a
 * creature without shadow can't be blocked by creatures with shadow."
 *
 * The second half is the one that used to be missing: [com.wingedsheep.engine.mechanics.combat.rules.ShadowRule]
 * only checked the attacker, so a Soltari or Dauthi creature could illegally block a ground
 * creature. Note this is *unlike* horsemanship, which CR 702.31b deliberately makes one-way.
 */
class ShadowEvasionTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of(
                "Swamp" to 10,
                "Forest" to 10,
                "Grizzly Bears" to 10,
                "Shadow Creature" to 10
            ),
            skipMulligans = true
        )
        return driver
    }

    fun GameTestDriver.advanceToPlayer1DeclareAttackers() {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    test("creature without shadow cannot block a creature with shadow") {
        val driver = createDriver()

        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Shadow Creature")
        driver.removeSummoningSickness(attacker)
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2).isSuccess shouldBe true
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(blocker to listOf(attacker)))
        )

        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "shadow"
        result.error shouldContainIgnoringCase "cannot block"
    }

    test("creature with shadow cannot block a creature without shadow") {
        val driver = createDriver()

        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.removeSummoningSickness(attacker)
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Shadow Creature")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2).isSuccess shouldBe true
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        val result = driver.submitExpectFailure(
            DeclareBlockers(driver.player2, mapOf(blocker to listOf(attacker)))
        )

        result.isSuccess shouldBe false
        result.error shouldContainIgnoringCase "shadow"
        result.error shouldContainIgnoringCase "cannot block"
    }

    test("creature with shadow CAN block a creature with shadow") {
        val driver = createDriver()

        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Shadow Creature")
        driver.removeSummoningSickness(attacker)
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Shadow Creature")
        driver.removeSummoningSickness(blocker)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2).isSuccess shouldBe true
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        driver.declareBlockers(
            driver.player2,
            mapOf(blocker to listOf(attacker))
        ).isSuccess shouldBe true
    }

    test("a shadow attacker connects when the defender has only non-shadow blockers") {
        val driver = createDriver()

        val attacker = driver.putCreatureOnBattlefield(driver.player1, "Shadow Creature")
        driver.removeSummoningSickness(attacker)
        val blocker = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")
        driver.removeSummoningSickness(blocker)

        val startingLife = driver.getLifeTotal(driver.player2)

        driver.advanceToPlayer1DeclareAttackers()
        driver.declareAttackers(driver.player1, listOf(attacker), driver.player2).isSuccess shouldBe true
        driver.bothPass()
        driver.currentStep shouldBe Step.DECLARE_BLOCKERS

        driver.declareBlockers(driver.player2, emptyMap()).isSuccess shouldBe true
        driver.bothPass()
        driver.bothPass()

        driver.getLifeTotal(driver.player2) shouldBe startingLife - 2
    }
})
