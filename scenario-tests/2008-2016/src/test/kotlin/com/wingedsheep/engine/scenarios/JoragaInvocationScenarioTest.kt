package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Joraga Invocation (ORI #183).
 *
 * {4}{G}{G} Sorcery
 * "Each creature you control gets +3/+3 until end of turn and must be blocked this turn if able."
 *
 * Covers both halves of the effect (the pump and the must-be-blocked requirement), that it is
 * scoped to creatures *you* control, and that only creatures present at resolution are affected
 * (CR 611.2c).
 */
class JoragaInvocationScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("each creature you control gets +3/+3; opponent's creatures are untouched") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        val giant = driver.putCreatureOnBattlefield(you, "Hill Giant")
        val theirBears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val spell = driver.putCardInHand(you, "Joraga Invocation")
        driver.giveMana(you, Color.GREEN, 6)
        driver.castSpell(you, spell).error shouldBe null
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.getPower(bears) shouldBe 5
        projected.getToughness(bears) shouldBe 5
        projected.getPower(giant) shouldBe 6
        projected.getToughness(giant) shouldBe 6
        // "Each creature you control" — the opponent's copy is unaffected.
        projected.getPower(theirBears) shouldBe 2
        projected.getToughness(theirBears) shouldBe 2
    }

    test("a creature that arrives after resolution gets neither the pump nor the requirement") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        val spell = driver.putCardInHand(you, "Joraga Invocation")
        driver.giveMana(you, Color.GREEN, 6)
        driver.castSpell(you, spell).error shouldBe null
        driver.bothPass()

        // Arrives only after the spell has already resolved (CR 611.2c).
        val latecomer = driver.putCreatureOnBattlefield(you, "Hill Giant")

        val projected = projector.project(driver.state)
        projected.getPower(latecomer) shouldBe 3
        projected.getToughness(latecomer) shouldBe 3
    }

    test("a pumped attacker must be blocked if able — declining to block is illegal") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        driver.removeSummoningSickness(bears)
        val blocker = driver.putCreatureOnBattlefield(opponent, "Hill Giant")

        val spell = driver.putCardInHand(you, "Joraga Invocation")
        driver.giveMana(you, Color.GREEN, 6)
        driver.castSpell(you, spell).error shouldBe null
        driver.bothPass()

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(bears), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        // The Hill Giant is able to block, so "no blockers" violates the requirement.
        driver.declareNoBlockers(opponent).isSuccess shouldBe false
        driver.declareBlockers(opponent, mapOf(blocker to listOf(bears))).isSuccess shouldBe true
    }
})
