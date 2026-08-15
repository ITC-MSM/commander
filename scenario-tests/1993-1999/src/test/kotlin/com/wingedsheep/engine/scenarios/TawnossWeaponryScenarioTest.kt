package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.TawnossWeaponry
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tawnos's Weaponry — the "tap-locked" +1/+1 buff plus optional self untap-skip.
 *
 * {2}, {T}: Target creature gets +1/+1 for as long as this artifact remains tapped.
 * You may choose not to untap this artifact during your untap step.
 */
class TawnossWeaponryScenarioTest : FunSpec({

    val abilityId = TawnossWeaponry.activatedAbilities.first().id
    val projector = StateProjector()

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        return d
    }

    fun GameTestDriver.activate(player: com.wingedsheep.sdk.model.EntityId, weaponry: com.wingedsheep.sdk.model.EntityId, target: com.wingedsheep.sdk.model.EntityId) {
        giveMana(player, Color.WHITE, 2)
        submit(
            ActivateAbility(
                playerId = player,
                sourceId = weaponry,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(target))
            )
        )
        bothPass()
    }

    test("buff applies and persists while Weaponry stays tapped, drops when it untaps") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val weaponry = d.putPermanentOnBattlefield(p, "Tawnos's Weaponry")
        val bear = d.putCreatureOnBattlefield(p, "Centaur Courser")
        d.removeSummoningSickness(weaponry)
        d.removeSummoningSickness(bear)

        d.activate(p, weaponry, bear)

        // Tapped from the {T} cost; the bear is 3/3 -> 4/4.
        d.state.getEntity(weaponry)?.has<TappedComponent>() shouldBe true
        projector.getProjectedPower(d.state, bear) shouldBe 4
        projector.getProjectedToughness(d.state, bear) shouldBe 4

        // Keep Weaponry tapped through the next untap step — buff survives.
        d.passPriorityUntil(Step.UNTAP)
        (d.pendingDecision is SelectCardsDecision) shouldBe true
        d.submitCardSelection(p, listOf(weaponry))
        d.state.getEntity(weaponry)?.has<TappedComponent>() shouldBe true
        projector.getProjectedPower(d.state, bear) shouldBe 4

        // Next untap step: untap Weaponry — buff ends immediately and does not return.
        d.passPriorityUntil(Step.UNTAP)
        d.submitCardSelection(p, emptyList())
        d.state.getEntity(weaponry)?.has<TappedComponent>() shouldBe false
        projector.getProjectedPower(d.state, bear) shouldBe 3
        projector.getProjectedToughness(d.state, bear) shouldBe 3
    }

    test("buff does not stack across turns while Weaponry stays tapped") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val weaponry = d.putPermanentOnBattlefield(p, "Tawnos's Weaponry")
        val bear = d.putCreatureOnBattlefield(p, "Centaur Courser")
        d.removeSummoningSickness(weaponry)
        d.removeSummoningSickness(bear)
        d.activate(p, weaponry, bear)

        val effectCount = d.state.floatingEffects.size
        d.passPriorityUntil(Step.UNTAP)
        d.submitCardSelection(p, listOf(weaponry))
        // Still +1/+1, not +2/+2, and no extra floating effect accrued.
        projector.getProjectedPower(d.state, bear) shouldBe 4
        d.state.floatingEffects.size shouldBe effectCount
    }
})
