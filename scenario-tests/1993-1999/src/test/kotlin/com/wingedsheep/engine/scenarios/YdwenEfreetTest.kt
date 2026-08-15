package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CoinFlipEvent
import com.wingedsheep.engine.state.components.combat.BlockedComponent
import com.wingedsheep.engine.state.components.combat.BlockingComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.arn.cards.YdwenEfreet
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Ydwen Efreet.
 *
 * Ydwen Efreet: {R}{R}{R}
 * Creature — Efreet 3/6
 * Whenever this creature blocks, flip a coin. If you lose the flip, remove this creature
 * from combat and it can't block this turn. Creatures it was blocking that had become
 * blocked by only this creature this combat become unblocked.
 */
class YdwenEfreetTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(YdwenEfreet)
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            startingLife = 20
        )
        return driver
    }

    /**
     * Walk the driver from the active player's first main phase to the opponent's declare-blockers
     * step with [attacker] (controlled by the active player) attacking the opponent and [blockers]
     * (controlled by the opponent) declared as blockers of [attacker].
     */
    fun setupAttackAndBlock(
        driver: GameTestDriver,
        attacker: EntityId,
        blockers: List<EntityId>,
    ): Pair<EntityId, EntityId> {
        val attackingPlayer = driver.activePlayer!!
        val defendingPlayer = driver.getOpponent(attackingPlayer)
        driver.removeSummoningSickness(attacker)
        blockers.forEach(driver::removeSummoningSickness)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attackingPlayer, listOf(attacker), defendingPlayer)
        driver.bothPass()

        driver.declareBlockers(defendingPlayer, blockers.associateWith { listOf(attacker) })
        return attackingPlayer to defendingPlayer
    }

    test("blocking triggers the coin flip ability") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val active = driver.activePlayer!!
        val defender = driver.getOpponent(active)
        val attacker = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val ydwen = driver.putCreatureOnBattlefield(defender, "Ydwen Efreet")

        setupAttackAndBlock(driver, attacker, listOf(ydwen))

        // After blockers are declared, the flip-coin ability is on the stack.
        driver.stackSize shouldBe 1

        // Resolve the trigger
        val result = driver.bothPass()
        val flips = result.events.filterIsInstance<CoinFlipEvent>()
        flips.size shouldBe 1
        flips[0].sourceName shouldBe "Ydwen Efreet"
    }

    test("losing the flip removes Ydwen from combat and unblocks its sole-blocked attacker") {
        var sawLoss = false
        repeat(50) {
            if (sawLoss) return@repeat

            val driver = createDriver()
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val active = driver.activePlayer!!
            val defender = driver.getOpponent(active)
            val attacker = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
            val ydwen = driver.putCreatureOnBattlefield(defender, "Ydwen Efreet")

            setupAttackAndBlock(driver, attacker, listOf(ydwen))
            val result = driver.bothPass()

            val flip = result.events.filterIsInstance<CoinFlipEvent>().firstOrNull() ?: return@repeat
            if (flip.won) return@repeat
            sawLoss = true

            // Ydwen left combat: no BlockingComponent, no BlockedComponent.
            driver.state.getEntity(ydwen)!!.has<BlockingComponent>().shouldBeFalse()

            // Sole-blocked attacker becomes unblocked: BlockedComponent is gone, defending
            // player takes the attacker's full damage in the damage step.
            driver.state.getEntity(attacker)!!.has<BlockedComponent>().shouldBeFalse()

            val defenderLifeBefore = driver.getLifeTotal(defender)
            driver.passPriorityUntil(Step.END_COMBAT)
            driver.getLifeTotal(defender) shouldBe defenderLifeBefore - 2  // Grizzly Bears is 2/2
        }
        sawLoss.shouldBeTrue()
    }

    test("winning the flip keeps Ydwen blocking and combat damage resolves normally") {
        var sawWin = false
        repeat(50) {
            if (sawWin) return@repeat

            val driver = createDriver()
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val active = driver.activePlayer!!
            val defender = driver.getOpponent(active)
            val attacker = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
            val ydwen = driver.putCreatureOnBattlefield(defender, "Ydwen Efreet")

            setupAttackAndBlock(driver, attacker, listOf(ydwen))
            val result = driver.bothPass()

            val flip = result.events.filterIsInstance<CoinFlipEvent>().firstOrNull() ?: return@repeat
            if (!flip.won) return@repeat
            sawWin = true

            // Ydwen stays in combat, the attacker stays blocked.
            driver.state.getEntity(ydwen)!!.get<BlockingComponent>()
                ?.blockedAttackerIds?.contains(attacker)?.shouldBeTrue()
            driver.state.getEntity(attacker)!!.has<BlockedComponent>().shouldBeTrue()

            // Resolve combat damage. Grizzly Bears (2/2) deals 2 to Ydwen (3/6), Ydwen deals 3
            // back, Grizzly Bears dies. Defending player takes no damage.
            val defenderLifeBefore = driver.getLifeTotal(defender)
            driver.passPriorityUntil(Step.END_COMBAT)
            driver.getLifeTotal(defender) shouldBe defenderLifeBefore
            driver.findPermanent(active, "Grizzly Bears").shouldBeNull()
            driver.findPermanent(defender, "Ydwen Efreet") shouldNotBe null
        }
        sawWin.shouldBeTrue()
    }

    test("with a second blocker, losing the flip keeps the attacker blocked by the other blocker") {
        var sawLoss = false
        repeat(50) {
            if (sawLoss) return@repeat

            val driver = createDriver()
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val active = driver.activePlayer!!
            val defender = driver.getOpponent(active)
            val attacker = driver.putCreatureOnBattlefield(active, "Centaur Courser")
            val ydwen = driver.putCreatureOnBattlefield(defender, "Ydwen Efreet")
            val coBlocker = driver.putCreatureOnBattlefield(defender, "Grizzly Bears")

            setupAttackAndBlock(driver, attacker, listOf(ydwen, coBlocker))
            val result = driver.bothPass()

            val flip = result.events.filterIsInstance<CoinFlipEvent>().firstOrNull() ?: return@repeat
            if (flip.won) return@repeat
            sawLoss = true

            // Ydwen is out of combat...
            driver.state.getEntity(ydwen)!!.has<BlockingComponent>().shouldBeFalse()
            // ...but the attacker remains blocked — it was NOT blocked by only Ydwen.
            val blocked = driver.state.getEntity(attacker)!!.get<BlockedComponent>()
            blocked shouldNotBe null
            blocked!!.blockerIds shouldBe listOf(coBlocker)
        }
        sawLoss.shouldBeTrue()
    }
})
