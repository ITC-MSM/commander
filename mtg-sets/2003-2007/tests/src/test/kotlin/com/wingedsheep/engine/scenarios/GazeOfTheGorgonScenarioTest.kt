package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.Outcome
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Gaze of the Gorgon (RAV #246) — "Regenerate target creature. At this turn's next end of combat,
 * destroy all creatures that blocked or were blocked by it this turn."
 *
 * Covers the regeneration, the end-of-combat sweep of every creature that blocked the target
 * (whether the blocks came before or after the Gaze), the target leaving the battlefield before the
 * sweep, and a creature that never fought the target surviving.
 */
class GazeOfTheGorgonScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingLife = 20)
        return d
    }

    fun GameTestDriver.castGaze(caster: EntityId, target: EntityId) {
        // After blocks the defending player may hold priority; hand it to the caster.
        state.priorityPlayerId?.let { holder -> if (holder != caster) passPriority(holder) }
        val gaze = putCardInHand(caster, "Gaze of the Gorgon")
        giveMana(caster, Color.BLACK, 1)
        giveColorlessMana(caster, 3)
        castSpell(caster, gaze, listOf(target)).outcome shouldBe Outcome.Done
        bothPass()
    }

    test("blockers of the target are destroyed at end of combat and the target regenerates") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        d.removeSummoningSickness(bears)
        val courser = d.putCreatureOnBattlefield(p2, "Centaur Courser")
        val bystander = d.putCreatureOnBattlefield(p2, "Craw Wurm")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(bears), p2)
        d.bothPass()
        d.declareBlockers(p2, mapOf(courser to listOf(bears)))

        // The block already happened when the Gaze is cast — it still counts.
        d.castGaze(p1, bears)

        d.passPriorityUntil(Step.END_COMBAT)
        d.bothPass() // resolve the end-of-combat destruction
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // The Bears took 3 lethal damage but regenerated.
        d.findPermanent(p1, "Grizzly Bears") shouldBe bears
        d.getGraveyardCardNames(p2) shouldContain "Centaur Courser"
        d.findPermanent(p2, "Craw Wurm") shouldBe bystander
    }

    test("an attacker that was blocked by the target is destroyed") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        d.removeSummoningSickness(bears)
        val wall = d.putCreatureOnBattlefield(p2, "Wall of Wood")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(bears), p2)
        d.bothPass()
        d.declareBlockers(p2, mapOf(wall to listOf(bears)))
        // Target the blocking Wall: the attacker it blocked is what gets destroyed.
        d.castGaze(p1, wall)

        d.passPriorityUntil(Step.END_COMBAT)
        d.bothPass()
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        d.getGraveyardCardNames(p1) shouldContain "Grizzly Bears"
        d.findPermanent(p2, "Wall of Wood") shouldBe wall
    }

    test("cast before blocks, the creatures that block afterwards are destroyed") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val wurm = d.putCreatureOnBattlefield(p1, "Craw Wurm")
        d.removeSummoningSickness(wurm)
        val bears = d.putCreatureOnBattlefield(p2, "Grizzly Bears")

        d.castGaze(p1, wurm)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(wurm), p2)
        d.bothPass()
        d.declareBlockers(p2, mapOf(bears to listOf(wurm)))
        d.passPriorityUntil(Step.END_COMBAT)
        d.bothPass()
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        d.getGraveyardCardNames(p2) shouldContain "Grizzly Bears"
        d.findPermanent(p1, "Craw Wurm") shouldBe wurm
    }

    test("the sweep happens even if the targeted creature has left the battlefield") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        d.removeSummoningSickness(bears)
        val courser = d.putCreatureOnBattlefield(p2, "Centaur Courser")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(bears), p2)
        d.bothPass()
        d.declareBlockers(p2, mapOf(courser to listOf(bears)))
        d.castGaze(p1, bears)

        // The Bears leave before end of combat.
        d.moveToGraveyard(bears)

        d.passPriorityUntil(Step.END_COMBAT)
        d.bothPass()
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        d.getGraveyardCardNames(p2) shouldContain "Centaur Courser"
    }

    test("cast after the turn's last combat, there is no next end of combat and nothing is destroyed") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        d.removeSummoningSickness(bears)
        // Wall of Wood (0/3) blocks the Bears and both survive combat.
        val wall = d.putCreatureOnBattlefield(p2, "Wall of Wood")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(p1, listOf(bears), p2)
        d.bothPass()
        d.declareBlockers(p2, mapOf(wall to listOf(bears)))
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // The Wall blocked the target this turn, but "this turn's next end of combat" never comes.
        d.castGaze(p1, bears)
        d.passPriorityUntil(Step.UPKEEP)

        d.getGraveyardCardNames(p2) shouldNotContain "Wall of Wood"
        d.state.delayedTriggers.size shouldBe 0
    }
})
