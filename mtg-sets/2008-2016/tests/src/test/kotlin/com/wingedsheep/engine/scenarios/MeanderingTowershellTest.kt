package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.cards.MeanderingTowershell
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Meandering Towershell.
 *
 * Meandering Towershell: {3}{G}{G}
 * Creature — Turtle
 * 5/9
 * Islandwalk
 * Whenever Meandering Towershell attacks, exile it. Return it to the battlefield
 * under your control tapped and attacking at the beginning of the declare attackers
 * step on your next turn.
 *
 * Implementation note: The delayed trigger fires at BEGINNING_OF_COMBAT rather than
 * DECLARE_ATTACKERS because the engine skips DECLARE_ATTACKERS when there are no
 * valid attackers. BEGINNING_OF_COMBAT always fires, and the creature enters
 * tapped and attacking before normal attacker declaration.
 */
class MeanderingTowershellTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MeanderingTowershell))
        return driver
    }

    /**
     * Pass priority until we reach the given step on the given player's turn.
     */
    fun GameTestDriver.passPriorityUntilPlayerStep(playerId: EntityId, targetStep: Step, maxPasses: Int = 10) {
        var loops = 0
        while (loops < maxPasses) {
            passPriorityUntil(targetStep)
            if (state.activePlayerId == playerId) return
            // Not the right player's turn — advance past this step
            bothPass()
            loops++
        }
        throw AssertionError("Failed to reach step $targetStep for player $playerId after $maxPasses loops (current: step=${state.step}, active=${state.activePlayerId})")
    }

    test("Meandering Towershell is exiled when it attacks and creates delayed trigger") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Island" to 20),
            startingLife = 20
        )

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val towershell = driver.putCreatureOnBattlefield(attacker, "Meandering Towershell")
        driver.removeSummoningSickness(towershell)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(towershell), defender)

        // After the attack trigger resolves, Towershell should be exiled
        driver.bothPass()

        driver.getExileCardNames(attacker) shouldContain "Meandering Towershell"
        driver.findPermanent(attacker, "Meandering Towershell") shouldBe null

        // A delayed trigger should have been created
        driver.state.delayedTriggers.size shouldBe 1
        driver.state.delayedTriggers.first().fireAtStep shouldBe Step.BEGIN_COMBAT
        // Gated to the controller's turn via fireOnPlayer = PlayerRef(You).
        driver.state.delayedTriggers.first().fireOnPlayerId shouldBe attacker
    }

    test("Meandering Towershell does not return on opponent's turn") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Island" to 20),
            startingLife = 20
        )

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val towershell = driver.putCreatureOnBattlefield(attacker, "Meandering Towershell")
        driver.removeSummoningSickness(towershell)

        // Attack with Towershell — it gets exiled
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(towershell), defender)
        driver.bothPass()

        // Advance to the opponent's beginning of combat step
        driver.passPriorityUntilPlayerStep(defender, Step.BEGIN_COMBAT)

        // Creature should still be in exile — delayed trigger should NOT have fired
        driver.getExileCardNames(attacker) shouldContain "Meandering Towershell"
        driver.state.delayedTriggers.size shouldBe 1
    }

    test("Meandering Towershell returns tapped and attacking on controller's next turn") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Island" to 20),
            startingLife = 20
        )

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val towershell = driver.putCreatureOnBattlefield(attacker, "Meandering Towershell")
        driver.removeSummoningSickness(towershell)

        // Attack with Towershell — it gets exiled
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(towershell), defender)
        driver.bothPass()

        // Advance to the controller's next beginning of combat step
        driver.passPriorityUntilPlayerStep(attacker, Step.BEGIN_COMBAT)

        // The delayed trigger fires and goes on the stack. Resolve it.
        driver.bothPass()

        val returned = driver.findPermanent(attacker, "Meandering Towershell")
        returned shouldNotBe null

        // It should be tapped
        driver.state.getEntity(returned!!)?.has<TappedComponent>() shouldBe true

        // It should be attacking
        val attackingComponent = driver.state.getEntity(returned)?.get<AttackingComponent>()
        attackingComponent shouldNotBe null
        attackingComponent!!.defenderId shouldBe defender

        // It should no longer be in exile
        driver.getExileCardNames(attacker) shouldNotContain "Meandering Towershell"

        // Delayed trigger should be consumed
        driver.state.delayedTriggers.size shouldBe 0
    }

    test("Meandering Towershell deals combat damage when it returns attacking") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Island" to 20),
            startingLife = 20
        )

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val towershell = driver.putCreatureOnBattlefield(attacker, "Meandering Towershell")
        driver.removeSummoningSickness(towershell)

        // Attack — gets exiled
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(towershell), defender)
        driver.bothPass()

        // Advance to controller's next beginning of combat
        driver.passPriorityUntilPlayerStep(attacker, Step.BEGIN_COMBAT)
        driver.bothPass() // resolve delayed trigger — Towershell returns tapped and attacking

        // Now Towershell is attacking. Advance through combat.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // Towershell (5/9) should have dealt 5 damage
        driver.assertLifeTotal(defender, 15)
    }
})
