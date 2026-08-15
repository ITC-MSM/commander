package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.AshnodsBattleGear
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Ashnod's Battle Gear — tap-locked +2/-2 on a creature you control.
 *
 * {2}, {T}: Target creature you control gets +2/-2 for as long as this artifact remains tapped.
 */
class AshnodsBattleGearScenarioTest : FunSpec({

    val abilityId = AshnodsBattleGear.activatedAbilities.first().id
    val projector = StateProjector()

    test("+2/-2 applies while tapped and reverts when Battle Gear untaps") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gear = d.putPermanentOnBattlefield(p, "Ashnod's Battle Gear")
        val bear = d.putCreatureOnBattlefield(p, "Centaur Courser") // 3/3
        d.removeSummoningSickness(gear)
        d.removeSummoningSickness(bear)

        d.giveMana(p, Color.WHITE, 2)
        d.submit(
            ActivateAbility(
                playerId = p,
                sourceId = gear,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(bear))
            )
        )
        d.bothPass()

        d.state.getEntity(gear)?.has<TappedComponent>() shouldBe true
        // 3/3 -> 5/1
        projector.getProjectedPower(d.state, bear) shouldBe 5
        projector.getProjectedToughness(d.state, bear) shouldBe 1

        // Untap Battle Gear on the next untap step -> buff ends, back to 3/3.
        d.passPriorityUntil(Step.UNTAP)
        d.submitCardSelection(p, emptyList())
        d.state.getEntity(gear)?.has<TappedComponent>() shouldBe false
        projector.getProjectedPower(d.state, bear) shouldBe 3
        projector.getProjectedToughness(d.state, bear) shouldBe 3
    }
})
