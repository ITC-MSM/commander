package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Wisecrack (SPM) — {2}{R} Instant.
 *
 *  "Target creature deals damage equal to its power to itself. If that creature is attacking,
 *   Wisecrack deals 2 damage to that creature's controller."
 *
 * Verifies a 3/3 deals 3 to itself and dies; and when the targeted creature is attacking, its
 * controller also takes 2 damage.
 */
class WisecrackScenarioTest : FunSpec({

    fun GameTestDriver.advanceToPlayerDeclareAttackers(player: com.wingedsheep.sdk.model.EntityId) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (activePlayer != player && safety < 50) {
            bothPass()
            passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
    }

    test("a non-attacking 3/3 takes 3 damage from itself and dies") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val giant = driver.putCreatureOnBattlefield(opp, "Hill Giant")
        val wisecrack = driver.putCardInHand(me, "Wisecrack")

        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, wisecrack, targets = listOf(giant))
        driver.bothPass()

        // The 3/3 dealt 3 to itself -> lethal -> dies.
        driver.getGraveyardCardNames(opp).contains("Hill Giant") shouldBe true
        driver.getLifeTotal(opp) shouldBe 20 // not attacking, so no extra 2 to controller
    }

    test("an attacking 3/3 takes 3 to itself AND its controller takes 2") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)

        val p1 = driver.player1
        val p2 = driver.player2

        // p2 attacks p1 with a 3/3 Hill Giant; p1 responds with Wisecrack on the attacker.
        val giant = driver.putCreatureOnBattlefield(p2, "Hill Giant")
        driver.removeSummoningSickness(giant)
        val wisecrack = driver.putCardInHand(p1, "Wisecrack")

        driver.advanceToPlayerDeclareAttackers(p2)
        driver.declareAttackers(p2, listOf(giant), p1)

        // The attacker is locked in. The active player (p2) has priority first — pass it so the
        // non-active player (p1) can respond with Wisecrack while the giant is still attacking.
        if (driver.state.priorityPlayerId == p2) driver.passPriority(p2)

        driver.giveColorlessMana(p1, 2)
        driver.giveMana(p1, Color.RED, 1)
        val p2LifeBefore = driver.getLifeTotal(p2)
        driver.castSpell(p1, wisecrack, targets = listOf(giant)).isSuccess shouldBe true
        driver.bothPass()

        // The attacker dealt 3 to itself and died, and its controller (p2) took 2.
        driver.getGraveyardCardNames(p2).contains("Hill Giant") shouldBe true
        driver.getLifeTotal(p2) shouldBe p2LifeBefore - 2
    }
})
