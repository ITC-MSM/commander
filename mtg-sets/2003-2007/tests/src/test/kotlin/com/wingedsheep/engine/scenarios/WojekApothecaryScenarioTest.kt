package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.WojekApothecary
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Wojek Apothecary (RAV #36) — "Radiance — {T}: Prevent the next 1 damage that would be dealt to
 * target creature and each other creature that shares a color with it this turn."
 *
 * The radiance group carries a prevention shield, and each creature in it gets a shield **of its
 * own**: 1 damage prevented on every white creature, not 1 damage prevented across the group. The
 * two bolts below are there to catch a group-scoped shield that would spend itself on the first
 * one and leave the second bare.
 */
class WojekApothecaryScenarioTest : FunSpec({

    val apothecaryAbility = WojekApothecary.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + WojekApothecary)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.damageOn(id: EntityId): Int = state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    fun GameTestDriver.shield(source: EntityId, target: EntityId) {
        submit(
            ActivateAbility(player1, source, apothecaryAbility, targets = listOf(ChosenTarget.Permanent(target)))
        ).isSuccess shouldBe true
        // The activator keeps priority after putting the ability on the stack.
        passPriority(player2)
        bothPass()
    }

    fun GameTestDriver.bolt(victim: EntityId) {
        giveMana(player1, Color.RED, 1)
        val card = putCardInHand(player1, "Lightning Bolt")
        castSpellWithTargets(player1, card, listOf(ChosenTarget.Permanent(victim))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 10) bothPass()
    }

    test("every white creature gets its own 1-damage shield; other colors get none") {
        val d = driver()
        val me = d.player1
        val opp = d.player2
        val apothecary = d.putCreatureOnBattlefield(me, "Wojek Apothecary").also { d.removeSummoningSickness(it) }
        val mine = d.putCreatureOnBattlefield(me, "Morph Test Creature")    // {2}{W} 2/3 — the target
        val theirs = d.putCreatureOnBattlefield(opp, "Morph Test Creature") // {2}{W} 2/3 — radiated onto
        val zombie = d.putCreatureOnBattlefield(opp, "Black Creature")      // {1}{B} 2/2 — unshielded

        d.shield(apothecary, mine)

        d.bolt(mine)
        d.bolt(theirs)
        d.bolt(zombie)

        withClue("the target's own shield ate 1 of the 3") {
            d.damageOn(mine) shouldBe 2
        }
        withClue("the second white creature had a shield of its own, not a share of one") {
            d.damageOn(theirs) shouldBe 2
        }
        withClue("black shares no color with a white target, so all 3 landed and killed a 2/2") {
            d.findPermanent(opp, "Black Creature").shouldBeNull()
        }
        withClue("the ability's tap cost was paid") {
            d.isTapped(apothecary) shouldBe true
        }
    }

    test("the shield is spent by the first damage and does not prevent a second point") {
        val d = driver()
        val me = d.player1
        val apothecary = d.putCreatureOnBattlefield(me, "Wojek Apothecary").also { d.removeSummoningSickness(it) }
        val cleric = d.putCreatureOnBattlefield(me, "Morph Test Creature")  // 2/3

        d.shield(apothecary, cleric)

        d.bolt(cleric)
        withClue("3 damage, 1 prevented") {
            d.damageOn(cleric) shouldBe 2
        }
        d.bolt(cleric)
        withClue("'the next 1 damage' is one shot — the second bolt is unprevented and kills it") {
            d.findPermanent(me, "Morph Test Creature").shouldBeNull()
        }
    }
})
