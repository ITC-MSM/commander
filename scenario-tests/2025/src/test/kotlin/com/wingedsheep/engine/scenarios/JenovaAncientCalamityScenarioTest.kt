package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.JenovaAncientCalamity
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Jenova, Ancient Calamity (FIN).
 *
 * - At the beginning of combat on your turn, put a number of +1/+1 counters equal to Jenova's
 *   power on up to one other target creature. That creature becomes a Mutant in addition to its
 *   other types.
 * - Whenever a Mutant you control dies during your turn, you draw cards equal to its power.
 *
 * Test 1 drives the combat trigger: a chosen other creature gains Jenova's power (1) in +1/+1
 * counters and the Mutant subtype (asserted via projected subtypes).
 *
 * Test 2 drives the dies payoff: the creature made a Mutant in test 1 is then destroyed on the
 * controller's turn, and the controller draws cards equal to that creature's last-known power.
 */
class JenovaAncientCalamityScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(JenovaAncientCalamity))
        return driver
    }

    fun plusOneCounters(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    // Player 1 may not be active at game start (random turn order) — advance until it is.
    fun GameTestDriver.advanceToPlayer1(targetStep: Step) {
        passPriorityUntil(targetStep)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(targetStep)
            safety++
        }
    }

    test("beginning of combat: target other creature gets Jenova's power in counters and becomes a Mutant") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player1, "Jenova, Ancient Calamity")

        driver.advanceToPlayer1(Step.BEGIN_COMBAT)

        // The combat trigger asks for "up to one other target creature".
        val targetDecision = driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(driver.player1, listOf(bear))
        driver.bothPass()

        // Jenova's power is 1, so one +1/+1 counter is placed on the chosen creature.
        plusOneCounters(driver, bear) shouldBe 1

        // The creature becomes a Mutant in addition to its other types (projected subtype grant).
        val projected = projector.project(driver.state)
        projected.getSubtypes(bear).any { it.equals("Mutant", ignoreCase = true) } shouldBe true
        // Still a Bear ("in addition to").
        projected.getSubtypes(bear).any { it.equals("Bear", ignoreCase = true) } shouldBe true
    }

    test("a Mutant you control dying during your turn draws cards equal to its power") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)

        val bear = driver.putCreatureOnBattlefield(driver.player1, "Grizzly Bears")
        driver.putCreatureOnBattlefield(driver.player1, "Jenova, Ancient Calamity")

        driver.advanceToPlayer1(Step.BEGIN_COMBAT)

        // Make the Bear a Mutant (and give it +1/+1, so it is a 3/3) via Jenova's combat trigger.
        val targetDecision = driver.pendingDecision as ChooseTargetsDecision
        driver.submitTargetSelection(driver.player1, listOf(bear))
        driver.bothPass()
        plusOneCounters(driver, bear) shouldBe 1

        // Advance to the controller's main phase so they can cast Doom Blade ("during your turn").
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        driver.activePlayer shouldBe driver.player1

        val handBefore = driver.getHandSize(driver.player1)

        // Destroy the Mutant for real so its dies trigger fires with last-known power (3).
        val doomBlade = driver.putCardInHand(driver.player1, "Doom Blade")
        driver.giveMana(driver.player1, Color.BLACK, 2)
        driver.castSpell(driver.player1, doomBlade, targets = listOf(bear)).isSuccess shouldBe true
        driver.bothPass() // resolve Doom Blade -> Bear is destroyed, queuing the dies trigger
        driver.state.getBattlefield().contains(bear) shouldBe false
        driver.bothPass() // resolve Jenova's "Mutant you control dies" draw trigger

        // The Bear was a 3/3 Mutant (2/2 base + one +1/+1 counter), so the controller drew 3 cards.
        driver.getHandSize(driver.player1) shouldBe handBefore + 3
    }
})
