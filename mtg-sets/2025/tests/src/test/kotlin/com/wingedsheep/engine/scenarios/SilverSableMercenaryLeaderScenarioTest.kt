package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Silver Sable, Mercenary Leader (SPM) — {2}{W} Legendary Creature 2/3.
 *
 *  "When Silver Sable enters, put a +1/+1 counter on another target creature.
 *   Whenever Silver Sable attacks, target modified creature you control gains lifelink
 *   until end of turn."
 *
 * The novel piece is the attack trigger's target restriction: it may only target a *modified*
 * creature you control (one with a counter / equipment / aura). This proves the
 * `StatePredicate.IsModified` filter constrains the legal targets, and that lifelink is granted.
 */
class SilverSableMercenaryLeaderScenarioTest : FunSpec({

    val projector = StateProjector()

    fun GameTestDriver.advanceToPlayer1DeclareAttackers() {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    test("attack trigger can only target a modified creature you control and grants it lifelink") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        val p1 = driver.player1
        val p2 = driver.player2

        val sable = driver.putCreatureOnBattlefield(p1, "Silver Sable, Mercenary Leader")
        driver.removeSummoningSickness(sable)

        // A modified creature (it has a +1/+1 counter) and an unmodified vanilla creature.
        val modified = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val plain = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        driver.replaceState(
            driver.state.updateEntity(modified) {
                it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
            }
        )

        driver.advanceToPlayer1DeclareAttackers()

        // Silver Sable attacks; the on-attack trigger fires and pauses for target selection.
        driver.declareAttackers(p1, listOf(sable), p2)
        driver.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()

        val decision = driver.pendingDecision as ChooseTargetsDecision
        val legalTargets = decision.legalTargets.values.flatten().toSet()

        // The modified creature is a legal target; the unmodified one is not. (Silver Sable
        // herself has no counter and is unmodified, so she is also excluded.)
        legalTargets.contains(modified) shouldBe true
        legalTargets.contains(plain) shouldBe false
        legalTargets.contains(sable) shouldBe false

        driver.submitTargetSelection(p1, listOf(modified))
        driver.bothPass()

        projector.project(driver.state).hasKeyword(modified, Keyword.LIFELINK) shouldBe true
    }
})
