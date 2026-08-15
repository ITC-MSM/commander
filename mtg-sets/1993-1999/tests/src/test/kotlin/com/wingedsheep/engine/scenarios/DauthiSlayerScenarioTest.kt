package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * Dauthi Slayer ({B}{B} Creature — Dauthi Soldier 2/2):
 * Shadow. This creature attacks each combat if able.
 */
class DauthiSlayerScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 40),
            skipMulligans = true
        )
        return driver
    }

    test("Dauthi Slayer has shadow") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val slayer = driver.putCreatureOnBattlefield(you, "Dauthi Slayer")
        val projected = projector.project(driver.state)
        projected.hasKeyword(slayer, Keyword.SHADOW) shouldBe true
    }

    test("Dauthi Slayer must attack each combat if able") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val slayer = driver.putCreatureOnBattlefield(you, "Dauthi Slayer")
        driver.removeSummoningSickness(slayer)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        // Declaring no attackers must fail — Dauthi Slayer is forced to attack.
        val noAttack = driver.submit(DeclareAttackers(playerId = you, attackers = emptyMap()))
        noAttack.isSuccess shouldBe false
        noAttack.error shouldContain "must attack"

        // Declaring it as an attacker succeeds.
        val attack = driver.submit(DeclareAttackers(playerId = you, attackers = mapOf(slayer to opponent)))
        attack.isSuccess shouldBe true
    }

    test("Dauthi Slayer is not forced to attack when it cannot attack (tapped)") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val slayer = driver.putCreatureOnBattlefield(you, "Dauthi Slayer")
        driver.removeSummoningSickness(slayer)
        // A tapped creature is unable to attack, so "attacks if able" does not force it.
        driver.tapPermanent(slayer)

        // A second, ready attacker keeps combat in this turn so we evaluate the forced-attack
        // check against the still-tapped Dauthi Slayer (otherwise the driver advances a turn).
        val bear = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        driver.removeSummoningSickness(bear)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        // Declaring only the bear succeeds — the tapped Dauthi Slayer is not forced to attack.
        val attack = driver.submit(DeclareAttackers(playerId = you, attackers = mapOf(bear to opponent)))
        attack.isSuccess shouldBe true
    }
})
