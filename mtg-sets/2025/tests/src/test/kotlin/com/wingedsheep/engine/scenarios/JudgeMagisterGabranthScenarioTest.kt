package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.JudgeMagisterGabranth
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Judge Magister Gabranth — {W}{B} 2/2 Legendary Creature — Human Advisor Knight (FIN).
 *
 * Menace.
 * Whenever another creature or artifact you control dies, put a +1/+1 counter on Gabranth.
 */
class JudgeMagisterGabranthScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(JudgeMagisterGabranth))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Swamp" to 20), startingLife = 20)
        return driver
    }

    test("has menace") {
        val driver = createDriver()
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val gabranth = driver.putCreatureOnBattlefield(me, "Judge Magister Gabranth")
        projector.project(driver.state).hasKeyword(gabranth, Keyword.MENACE) shouldBe true
    }

    test("another creature I control dying puts a +1/+1 counter on Gabranth") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val gabranth = driver.putCreatureOnBattlefield(me, "Judge Magister Gabranth")
        // A 1/1 of mine that will die in combat.
        val lions = driver.putCreatureOnBattlefield(me, "Savannah Lions")
        driver.removeSummoningSickness(lions)
        // A 3/3 blocker for the opponent.
        val courser = driver.putCreatureOnBattlefield(opp, "Centaur Courser")

        projector.project(driver.state).getPower(gabranth) shouldBe 2

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(lions), opp)
        driver.bothPass()
        driver.declareBlockers(opp, mapOf(courser to listOf(lions)))
        // Drive priority through combat damage (auto-resolving the damage board) and let the dies
        // trigger resolve.
        var guard = 0
        while (guard++ < 60) {
            val pd = driver.state.pendingDecision
            when {
                pd != null -> driver.autoResolveDecision()
                driver.findPermanent(me, "Savannah Lions") == null && driver.state.stack.isEmpty() -> break
                driver.state.priorityPlayerId != null -> driver.passPriority(driver.state.priorityPlayerId!!)
                else -> break
            }
        }

        // Savannah Lions died (3 >= 1 toughness); Gabranth grew to 3/3.
        driver.findPermanent(me, "Savannah Lions") shouldBe null
        projector.project(driver.state).getPower(driver.findPermanent(me, "Judge Magister Gabranth")!!) shouldBe 3
    }

    test("an opponent's creature dying does NOT trigger Gabranth") {
        val driver = createDriver()
        val me = driver.player1
        val opp = driver.player2

        val gabranth = driver.putCreatureOnBattlefield(me, "Judge Magister Gabranth")
        // My 3/3 attacker kills the opponent's 1/1 blocker.
        val courser = driver.putCreatureOnBattlefield(me, "Centaur Courser")
        driver.removeSummoningSickness(courser)
        val lions = driver.putCreatureOnBattlefield(opp, "Savannah Lions")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(courser), opp)
        driver.bothPass()
        driver.declareBlockers(opp, mapOf(lions to listOf(courser)))
        var guard = 0
        while (guard++ < 60) {
            val pd = driver.state.pendingDecision
            when {
                pd != null -> driver.autoResolveDecision()
                driver.findPermanent(opp, "Savannah Lions") == null && driver.state.stack.isEmpty() -> break
                driver.state.priorityPlayerId != null -> driver.passPriority(driver.state.priorityPlayerId!!)
                else -> break
            }
        }

        // The opponent's Lions died, but it isn't a creature I control — Gabranth stays 2/2.
        driver.findPermanent(opp, "Savannah Lions") shouldBe null
        projector.project(driver.state).getPower(gabranth) shouldBe 2
    }
})
