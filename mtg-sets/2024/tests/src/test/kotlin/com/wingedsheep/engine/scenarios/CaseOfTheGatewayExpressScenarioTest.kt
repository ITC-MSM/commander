package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheGatewayExpress
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Case of the Gateway Express — {1}{W} Enchantment — Case.
 *
 * Three things to prove, and the middle one is the new engine vocabulary:
 *
 *  1. the ETB is a *group* ping — each creature you control deals its own 1 damage, so the total
 *     scales with the board rather than being one point from the enchantment;
 *  2. "Three or more creatures attacked this turn" is the player-agnostic
 *     `Conditions.CreaturesAttackedThisTurn`, which reads the union of every player's attack record
 *     — two attackers must leave it unsolved and three must solve it;
 *  3. the Solved static only pumps once the Case is solved.
 */
class CaseOfTheGatewayExpressScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaseOfTheGatewayExpress)
        driver.initMirrorMatch(Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    /** Cast the Case aiming its enters trigger at [victim], and resolve both. */
    fun GameTestDriver.playCaseAt(victim: EntityId): EntityId {
        val card = putCardInHand(player1, "Case of the Gateway Express")
        giveMana(player1, Color.WHITE, 2)
        castSpell(player1, card).isSuccess shouldBe true
        bothPass() // resolve the Case; its enters trigger goes on the stack and asks for a target
        submitTargetSelection(player1, listOf(victim))
        bothPass() // resolve the trigger
        return card
    }

    /** Attack with [count] of your creatures, then walk the turn out to the end step. */
    fun GameTestDriver.attackWith(attackers: List<EntityId>) {
        attackers.forEach { removeSummoningSickness(it) }
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        declareAttackers(player1, attackers, player2)
    }

    test("each creature you control deals its own 1 damage — two creatures leave a 2/3 alive") {
        val driver = newDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val victim = driver.putCreatureOnBattlefield(driver.player2, "Minotaur Warrior") // 2/3

        driver.playCaseAt(victim)

        driver.state.getBattlefield().contains(victim) shouldBe true
    }

    test("three creatures deal three separate points — lethal to the same 2/3") {
        val driver = newDriver()
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val victim = driver.putCreatureOnBattlefield(driver.player2, "Minotaur Warrior") // 2/3

        driver.playCaseAt(victim)

        driver.state.getBattlefield().contains(victim) shouldBe false
    }

    test("with no creatures the enters trigger still resolves and deals nothing") {
        val driver = newDriver()
        val victim = driver.putCreatureOnBattlefield(driver.player2, "Minotaur Warrior")

        driver.playCaseAt(victim)

        driver.state.getBattlefield().contains(victim) shouldBe true
    }

    test("two attackers leave it unsolved; three solve it (CR 719.3a)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Gateway Express")
        val a = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val b = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")

        driver.attackWith(listOf(a, b))
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false

        // Next own turn, a third attacker joins.
        val c = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN) // opponent's turn
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN) // ours again

        driver.attackWith(listOf(a, b, c))
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
    }

    test("Solved — creatures you control get +1/+0, and only once solved (CR 702.169b)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Gateway Express")
        val a = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val b = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val c = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        val theirs = driver.putCreatureOnBattlefield(driver.player2, "Grizzly Bears")

        driver.state.projectedState.getPower(a) shouldBe 2

        driver.attackWith(listOf(a, b, c))
        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.isSolved(case) shouldBe true
        driver.state.projectedState.getPower(a) shouldBe 3
        // Symmetry check: it's "creatures you control", so the opponent's bear is untouched.
        driver.state.projectedState.getPower(theirs) shouldBe 2
    }
})
