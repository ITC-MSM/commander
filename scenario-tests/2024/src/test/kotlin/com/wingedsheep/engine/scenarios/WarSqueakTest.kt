package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.WarSqueak
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * War Squeak (BLB): "When this Aura enters, target creature an opponent controls
 * CAN'T BLOCK this turn."
 *
 * Regression guard: this used to be modeled as CantAttackOrBlock, which also stopped
 * the opponent's creature from attacking. The card only restricts blocking.
 */
class WarSqueakTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(WarSqueak))
        return driver
    }

    test("the targeted creature can't block but is NOT restricted from attacking") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val myCreature = driver.putCreatureOnBattlefield(active, "Savannah Lions")
        val theirCreature = driver.putCreatureOnBattlefield(opponent, "Savannah Lions")

        driver.giveMana(active, Color.RED, 1)
        val aura = driver.putCardInHand(active, "War Squeak")
        driver.castSpellWithTargets(active, aura, listOf(ChosenTarget.Permanent(myCreature)))
        repeat(4) {
            if (driver.pendingDecision == null && driver.state.priorityPlayerId != null) driver.bothPass()
        }

        // The ETB trigger asks for a target creature an opponent controls.
        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<ChooseTargetsDecision>()
        driver.submitTargetSelection(active, listOf(theirCreature))
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.cantBlock(theirCreature) shouldBe true
        // Oracle text only says "can't block" — attacking must stay legal.
        projected.cantAttack(theirCreature) shouldBe false
    }
})
