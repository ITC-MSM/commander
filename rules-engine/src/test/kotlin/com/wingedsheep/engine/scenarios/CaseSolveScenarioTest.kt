package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedActivatedAbility
import com.wingedsheep.sdk.dsl.solvedStaticAbility
import com.wingedsheep.sdk.dsl.solvedTriggeredAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Engine tests for the Case mechanic (CR 719) and the Solved keyword (CR 702.169).
 *
 * Three test Cases, one per shape the "Solved —" keyword can take, all sharing the same easily
 * flipped "to solve" condition ("your life total is 25 or more") so a test can decide whether the
 * Case solves by setting a life total.
 */
class CaseSolveScenarioTest : FunSpec({

    /** "Solved — Creatures you control get +1/+0." (CR 702.169b — a static ability) */
    val staticCase = card("Test Case Static") {
        manaCost = "{W}"
        typeLine = "Enchantment — Case"
        oracleText = "To solve — You have 25 or more life.\nSolved — Creatures you control get +1/+0."
        toSolve(Conditions.LifeAtLeast(25))
        solvedStaticAbility {
            ability = ModifyStats(1, 0, Filters.AllControlledCreatures)
        }
    }

    /** "Solved — At the beginning of your upkeep, draw a card." (CR 702.169c — a triggered ability) */
    val triggeredCase = card("Test Case Triggered") {
        manaCost = "{W}"
        typeLine = "Enchantment — Case"
        oracleText = "To solve — You have 25 or more life.\n" +
            "Solved — At the beginning of your upkeep, draw a card."
        toSolve(Conditions.LifeAtLeast(25))
        solvedTriggeredAbility {
            trigger = Triggers.YourUpkeep
            effect = Effects.DrawCards(1)
        }
    }

    /** "Solved — {T}: You gain 2 life." (CR 702.169d — an activated ability) */
    val activatedCase = card("Test Case Activated") {
        manaCost = "{W}"
        typeLine = "Enchantment — Case"
        oracleText = "To solve — You have 25 or more life.\nSolved — {T}: You gain 2 life."
        toSolve(Conditions.LifeAtLeast(25))
        solvedActivatedAbility {
            cost = AbilityCost.Tap
            effect = Effects.GainLife(2)
        }
    }

    /** A {W} instant that destroys target enchantment — a real zone change for the Case. */
    val breakTheCase = card("Break the Case") {
        manaCost = "{W}"
        typeLine = "Instant"
        oracleText = "Destroy target enchantment."
        spell {
            val t = target("target", TargetPermanent(filter = TargetFilter(GameObjectFilter.Enchantment)))
            effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true)
        }
    }

    /** A {U} instant that token-copies target enchantment — for the copiable-values check. */
    val copyTheCase = card("Copy the Case") {
        manaCost = "{U}"
        typeLine = "Instant"
        oracleText = "Create a token that's a copy of target enchantment."
        spell {
            val t = target("target", TargetPermanent(filter = TargetFilter(GameObjectFilter.Enchantment)))
            effect = Effects.CreateTokenCopyOfTarget(t)
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(staticCase)
        driver.registerCard(triggeredCase)
        driver.registerCard(activatedCase)
        driver.registerCard(breakTheCase)
        driver.registerCard(copyTheCase)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    /** Pass through this turn's end step, resolving anything that triggers there. */
    fun GameTestDriver.runThroughEndStep() {
        passPriorityUntil(Step.END)
        bothPass()
    }

    /**
     * Advance into the next player's precombat main phase. `passPriorityUntil` is a no-op when the
     * target step is the current one, so stepping through UPKEEP first is what actually crosses the
     * turn boundary.
     */
    fun GameTestDriver.beginNextTurn() {
        passPriorityUntil(Step.UPKEEP)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    test("a Case whose condition is met at the beginning of its controller's end step becomes solved (CR 719.3a)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Static")
        driver.setLifeTotal(driver.player1, 25)

        driver.isSolved(case) shouldBe false
        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe true
    }

    test("a Case whose condition is not met stays unsolved, and solves on a later end step (CR 719.3a)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Static")

        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe false

        // Opponent's turn, then back to ours with the condition now met.
        driver.beginNextTurn()
        driver.setLifeTotal(driver.player1, 25)
        driver.beginNextTurn()
        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe true
    }

    test("the trigger is at YOUR end step — an opponent's end step never solves it (CR 719.3a)") {
        val driver = newDriver()
        // Controlled by player 2, whose end step doesn't come until the following turn.
        val case = driver.putPermanentOnBattlefield(driver.player2, "Test Case Static")
        driver.setLifeTotal(driver.player2, 25)

        driver.runThroughEndStep() // player 1's end step
        driver.isSolved(case) shouldBe false

        driver.beginNextTurn() // now player 2's turn
        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe true
    }

    test("the intervening-if is re-checked on resolution — a condition undone in response leaves it unsolved (CR 603.4)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Static")
        driver.setLifeTotal(driver.player1, 25)

        // The trigger goes on the stack at the beginning of the end step, with the condition true.
        driver.passPriorityUntil(Step.END)
        driver.assertStackSize(1, "the 'to solve' trigger should be waiting on the stack")

        // Undo the condition before it resolves; CR 603.4 removes the ability from the stack.
        driver.setLifeTotal(driver.player1, 3)
        driver.bothPass()
        driver.isSolved(case) shouldBe false
    }

    test("solved is sticky — it survives cleanup and later turns (CR 719.3b)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Static")
        driver.setLifeTotal(driver.player1, 25)
        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe true

        // Drop back below the "to solve" threshold and cross two turn boundaries.
        driver.setLifeTotal(driver.player1, 3)
        driver.beginNextTurn()
        driver.beginNextTurn()
        driver.isSolved(case) shouldBe true
    }

    test("an already-solved Case does not re-trigger (CR 719.3a: 'and this Case is not solved')") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Static")
        driver.setLifeTotal(driver.player1, 25)
        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe true

        // Next own end step: with the condition still true, only the "not solved" half stops it.
        driver.beginNextTurn()
        driver.beginNextTurn()
        driver.passPriorityUntil(Step.END)
        driver.assertStackSize(0, "a solved Case must not put its 'to solve' trigger on the stack again")
    }

    test("the designation is lost when the Case leaves the battlefield (CR 719.3b / 400.7)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Static")
        driver.setLifeTotal(driver.player1, 25)
        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe true

        // Destroy it for real: the test driver's blunt moveToGraveyard helper skips the
        // zone-change component strip, which is exactly the code under test here.
        driver.beginNextTurn()
        driver.beginNextTurn()
        val removal = driver.putCardInHand(driver.player1, "Break the Case")
        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.castSpell(driver.player1, removal, listOf(case))
        driver.bothPass()

        driver.state.getBattlefield().contains(case) shouldBe false
        driver.isSolved(case) shouldBe false
    }

    test("solved is not a copiable value — a copy of a solved Case is unsolved (CR 719.3b)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Static")
        driver.setLifeTotal(driver.player1, 25)
        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe true

        driver.beginNextTurn()
        driver.beginNextTurn()
        val copySpell = driver.putCardInHand(driver.player1, "Copy the Case")
        driver.giveMana(driver.player1, Color.BLUE, 1)
        driver.castSpell(driver.player1, copySpell, listOf(case))
        driver.bothPass()

        val token = driver.getPermanents(driver.player1)
            .single { it != case && driver.getCardName(it) == "Test Case Static" }
        driver.isSolved(token) shouldBe false
        driver.isSolved(case) shouldBe true
    }

    test("Solved — static ability applies only while solved (CR 702.169b)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Static")
        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears") // 2/2

        driver.state.projectedState.getPower(bear) shouldBe 2

        driver.setLifeTotal(driver.player1, 25)
        driver.runThroughEndStep()

        driver.isSolved(case) shouldBe true
        driver.state.projectedState.getPower(bear) shouldBe 3
    }

    test("Solved — triggered ability triggers only if solved (CR 702.169c)") {
        val driver = newDriver()
        driver.putPermanentOnBattlefield(driver.player1, "Test Case Triggered")

        // Opponent's turn, then back to our upkeep while still unsolved: nothing triggers.
        driver.beginNextTurn()
        driver.passPriorityUntil(Step.UPKEEP)
        driver.assertStackSize(0, "an unsolved Case's Solved trigger must not trigger")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.setLifeTotal(driver.player1, 25)
        driver.runThroughEndStep()

        // Opponent's turn, then our upkeep again — now solved, so it triggers and draws.
        driver.beginNextTurn()
        val handBefore = driver.getHandSize(driver.player1)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.assertStackSize(1, "a solved Case's Solved trigger must trigger")
        driver.bothPass()
        driver.getHandSize(driver.player1) shouldBe handBefore + 1
    }

    test("Solved — activated ability can be activated only if solved (CR 702.169d)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Test Case Activated")

        fun canActivate(): Boolean =
            driver.legalActions(driver.player1).any { it.toString().contains(case.toString()) }

        canActivate() shouldBe false

        driver.setLifeTotal(driver.player1, 25)
        driver.runThroughEndStep()
        driver.isSolved(case) shouldBe true

        driver.beginNextTurn()
        driver.beginNextTurn()
        canActivate() shouldBe true
    }
})
