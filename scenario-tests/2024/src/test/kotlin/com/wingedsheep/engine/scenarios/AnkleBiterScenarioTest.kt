package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Ankle Biter (OTJ #153) — {G} Snake, 1/1, Deathtouch.
 *
 *   "Deathtouch"
 *
 * Verifies the vanilla-with-deathtouch creature: it carries the keyword in projected state, and
 * its single point of combat damage is lethal to anything it fights (CR 702.2), trading up into a
 * larger creature.
 */
class AnkleBiterScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("Ankle Biter has deathtouch in projected state") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val biter = driver.putCreatureOnBattlefield(player, "Ankle Biter")

        driver.state.projectedState.getPower(biter) shouldBe 1
        driver.state.projectedState.getToughness(biter) shouldBe 1
        driver.state.projectedState.hasKeyword(biter, Keyword.DEATHTOUCH) shouldBe true
    }

    test("blocking with Ankle Biter kills a larger attacker via deathtouch") {
        val driver = createDriver()
        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        // A 2/2 attacker — would normally survive 1 damage, but deathtouch makes it lethal.
        val bears = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        val biter = driver.putCreatureOnBattlefield(defender, "Ankle Biter")
        driver.removeSummoningSickness(bears)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(bears), defender).isSuccess shouldBe true

        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(defender, mapOf(biter to listOf(bears))).isSuccess shouldBe true

        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // Deathtouch: 1 damage from the Biter is lethal to the 2/2 attacker.
        driver.findPermanent(attacker, "Grizzly Bears") shouldBe null
        driver.getGraveyardCardNames(attacker) shouldContain "Grizzly Bears"

        // The 1/1 Biter also dies to the 2 damage it took.
        driver.findPermanent(defender, "Ankle Biter") shouldBe null
        driver.getGraveyardCardNames(defender) shouldContain "Ankle Biter"
    }
})
