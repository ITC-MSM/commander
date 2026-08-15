package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.MeteorStorm
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Invasion engine gap #9 — discard (at random) as an activation cost.
 *
 * Meteor Storm: "{2}{R}{G}, Discard two cards at random: This enchantment deals 4 damage to any
 * target." The "discard two cards at random" portion is paid automatically by the engine (no
 * player selection), via `Costs.DiscardAtRandom(2)`.
 */
class MeteorStormDiscardCostTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(MeteorStorm))
        return d
    }

    test("activating Meteor Storm discards two cards at random and deals 4 damage") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Forest" to 20), startingLife = 20)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val storm = d.putPermanentOnBattlefield(me, "Meteor Storm")
        val abilityId = MeteorStorm.activatedAbilities.first().id

        // Two cards to feed the random discard cost.
        d.putCardInHand(me, "Centaur Courser")
        d.putCardInHand(me, "Savannah Lions")
        val handBefore = d.getHandSize(me)
        val graveBefore = d.getGraveyard(me).size

        // {2}{R}{G}: pay from a pool of two red + two green.
        d.giveMana(me, Color.RED, 2)
        d.giveMana(me, Color.GREEN, 2)

        val result = d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = storm,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opp))
            )
        )
        result.isSuccess shouldBe true
        d.bothPass()

        // Exactly two cards discarded at random; they land in the graveyard.
        d.getHandSize(me) shouldBe handBefore - 2
        d.getGraveyard(me).size shouldBe graveBefore + 2

        // 4 damage to the opponent.
        d.getLifeTotal(opp) shouldBe 16
    }

    test("Meteor Storm cannot be activated without two cards to discard") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Forest" to 20), startingLife = 20)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val storm = d.putPermanentOnBattlefield(me, "Meteor Storm")
        val abilityId = MeteorStorm.activatedAbilities.first().id

        // Empty the hand — not enough cards for "discard two at random".
        var s = d.state
        d.getHand(me).forEach { s = s.removeFromZone(ZoneKey(me, Zone.HAND), it) }
        d.replaceState(s)
        d.giveMana(me, Color.RED, 2)
        d.giveMana(me, Color.GREEN, 2)

        d.submitExpectFailure(
            ActivateAbility(
                playerId = me,
                sourceId = storm,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(opp))
            )
        )
    }
})
