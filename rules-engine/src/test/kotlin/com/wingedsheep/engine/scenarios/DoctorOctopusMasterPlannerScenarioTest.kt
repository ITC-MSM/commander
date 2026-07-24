package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.spm.cards.DoctorOctopusMasterPlanner
import com.wingedsheep.mtg.sets.definitions.spm.cards.KravensCats
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Doctor Octopus, Master Planner (SPM) — {5}{U}{B} Legendary Creature — Human Scientist Villain 4/8.
 *
 *  "Other Villains you control get +2/+2.
 *   Your maximum hand size is eight.
 *   At the beginning of your end step, if you have fewer than eight cards in hand, draw cards equal
 *   to the difference."
 *
 * Verifies:
 *  - the anthem pumps another Villain, but not Doc Ock himself (excludeSelf) nor a non-Villain;
 *  - the end-step trigger fills the hand up to eight when below (intervening-if passes), drawing
 *    exactly the difference;
 *  - the trigger does not go on the stack when the hand is already at eight (Rule 603.4).
 */
class DoctorOctopusMasterPlannerScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        registerCard(DoctorOctopusMasterPlanner)
        registerCard(KravensCats)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    test("anthem gives +2/+2 to other Villains, but not to itself or a non-Villain") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val docOck = d.putPermanentOnBattlefield(you, "Doctor Octopus, Master Planner")
        val kravensCats = d.putPermanentOnBattlefield(you, "Kraven's Cats") // 2/2 Cat Villain
        val centaur = d.putPermanentOnBattlefield(you, "Centaur Courser")   // 3/3 non-Villain

        withClue("Kraven's Cats is another Villain you control, so it gets +2/+2 (2/2 -> 4/4)") {
            d.state.projectedState.getPower(kravensCats) shouldBe 4
            d.state.projectedState.getToughness(kravensCats) shouldBe 4
        }
        withClue("Doc Ock excludes himself from the anthem — stays the printed 4/8") {
            d.state.projectedState.getPower(docOck) shouldBe 4
            d.state.projectedState.getToughness(docOck) shouldBe 8
        }
        withClue("Centaur Courser is not a Villain, so it is unaffected (stays 3/3)") {
            d.state.projectedState.getPower(centaur) shouldBe 3
            d.state.projectedState.getToughness(centaur) shouldBe 3
        }
    }

    test("end step draws up to eight when the hand is below eight") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(you, "Doctor Octopus, Master Planner")

        // Empty the hand so the difference is unambiguous (8 - 0 = 8).
        d.getHand(you).toList().forEach { d.replaceState(d.state.removeFromZone(ZoneKey(you, Zone.HAND), it)) }
        d.getHandSize(you) shouldBe 0

        // Advance to the end step — intervening-if (fewer than eight) passes, so the ability fires.
        d.passPriorityUntil(Step.END)
        d.state.stack.size shouldBe 1
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        withClue("Drew cards equal to the difference (8 - 0), filling the hand to eight") {
            d.getHandSize(you) shouldBe 8
        }
    }

    test("end step does not trigger when the hand is already at eight") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(you, "Doctor Octopus, Master Planner")

        // Set the hand to exactly eight cards (Doc Ock's maximum hand size).
        d.getHand(you).toList().forEach { d.replaceState(d.state.removeFromZone(ZoneKey(you, Zone.HAND), it)) }
        repeat(8) { d.putCardInHand(you, "Forest") }
        d.getHandSize(you) shouldBe 8

        // At the end step the intervening-if fails, so the ability never goes on the stack.
        d.passPriorityUntil(Step.END)
        withClue("Hand is not fewer than eight, so no trigger is put on the stack (CR 603.4)") {
            d.state.stack.size shouldBe 0
            d.getHandSize(you) shouldBe 8
        }
    }
})
