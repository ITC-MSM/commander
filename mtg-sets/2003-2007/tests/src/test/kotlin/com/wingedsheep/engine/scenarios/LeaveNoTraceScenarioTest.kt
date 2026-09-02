package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.Blockbuster
import com.wingedsheep.mtg.sets.definitions.rav.cards.DoublingSeason
import com.wingedsheep.mtg.sets.definitions.rav.cards.GlareOfSubdual
import com.wingedsheep.mtg.sets.definitions.rav.cards.HalcyonGlaze
import com.wingedsheep.mtg.sets.definitions.rav.cards.LeaveNoTrace
import com.wingedsheep.mtg.sets.definitions.rav.cards.LightOfSanction
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Leave No Trace (RAV #23) — "Radiance — Destroy target enchantment and each other enchantment
 * that shares a color with it."
 *
 * Radiance over a non-creature target: a green-white enchantment radiates to the green and the
 * white enchantments on either side of the table, and leaves the blue and red ones alone.
 */
class LeaveNoTraceScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(
            TestCards.all + listOf(
                LeaveNoTrace, GlareOfSubdual, DoublingSeason, LightOfSanction, HalcyonGlaze, Blockbuster
            )
        )
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.leaveNoTrace(caster: EntityId, target: EntityId) {
        giveMana(caster, Color.WHITE, 2)
        val spell = putCardInHand(caster, "Leave No Trace")
        castSpellWithTargets(caster, spell, listOf(ChosenTarget.Permanent(target))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("a green-white target takes every other green or white enchantment with it, on both sides") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val glare = d.putPermanentOnBattlefield(opp, "Glare of Subdual")   // {2}{G}{W}
        d.putPermanentOnBattlefield(opp, "Doubling Season")               // {4}{G}
        d.putPermanentOnBattlefield(me, "Light of Sanction")              // {1}{W}{W} — mine, still destroyed
        d.putPermanentOnBattlefield(opp, "Halcyon Glaze")                 // {2}{U} — spared
        d.putPermanentOnBattlefield(me, "Blockbuster")                    // {3}{R}{R} — spared

        d.leaveNoTrace(me, glare)

        withClue("the target and every green or white enchantment were destroyed") {
            d.findPermanent(opp, "Glare of Subdual").shouldBeNull()
            d.findPermanent(opp, "Doubling Season").shouldBeNull()
            d.findPermanent(me, "Light of Sanction").shouldBeNull()
            d.getGraveyardCardNames(opp) shouldContainExactlyInAnyOrder listOf("Glare of Subdual", "Doubling Season")
            d.getGraveyardCardNames(me) shouldContainExactlyInAnyOrder listOf("Light of Sanction", "Leave No Trace")
        }
        withClue("the blue and red enchantments share no color with the target") {
            d.findPermanent(opp, "Halcyon Glaze").shouldNotBeNull()
            d.findPermanent(me, "Blockbuster").shouldNotBeNull()
        }
    }
})
