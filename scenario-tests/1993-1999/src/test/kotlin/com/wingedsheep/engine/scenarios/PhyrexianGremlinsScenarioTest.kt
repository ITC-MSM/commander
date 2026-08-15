package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.atq.cards.PhyrexianGremlins
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.core.ManaCost
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Phyrexian Gremlins — imposed untap suppression. {T} taps a target artifact and grants it
 * "doesn't untap during its controller's untap step for as long as Phyrexian Gremlins remains
 * tapped." The grant rides the WhileSourceTapped one-way latch keyed to Gremlins.
 */
class PhyrexianGremlinsScenarioTest : FunSpec({

    val abilityId = PhyrexianGremlins.activatedAbilities.first().id
    val projector = StateProjector()

    // A vanilla artifact for the victim.
    val Widget = CardDefinition.artifact(name = "Test Widget", manaCost = ManaCost.parse("{1}"))

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(Widget))
        return d
    }

    test("taps target artifact and keeps it from untapping while Gremlins stays tapped") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val me = d.activePlayer!!
        val opp = if (d.player1 == me) d.player2 else d.player1
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gremlins = d.putCreatureOnBattlefield(me, "Phyrexian Gremlins")
        d.removeSummoningSickness(gremlins)
        // The opponent controls an untapped artifact.
        val widget = d.putPermanentOnBattlefield(opp, "Test Widget")

        d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = gremlins,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(widget))
            )
        )
        d.bothPass()

        // Gremlins tapped from the {T} cost; the widget is tapped and now "doesn't untap".
        d.state.getEntity(gremlins)?.has<TappedComponent>() shouldBe true
        d.state.getEntity(widget)?.has<TappedComponent>() shouldBe true
        projector.project(d.state).hasKeyword(widget, AbilityFlag.DOESNT_UNTAP) shouldBe true

        // Reach the opponent's untap step: the widget must stay tapped (Gremlins is still tapped).
        d.passPriorityUntil(Step.UPKEEP) // opponent's beginning phase
        d.activePlayer shouldBe opp
        d.state.getEntity(widget)?.has<TappedComponent>() shouldBe true
    }

    test("once Gremlins untaps, the lock releases and the artifact untaps") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val me = d.activePlayer!!
        val opp = if (d.player1 == me) d.player2 else d.player1
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val gremlins = d.putCreatureOnBattlefield(me, "Phyrexian Gremlins")
        d.removeSummoningSickness(gremlins)
        val widget = d.putPermanentOnBattlefield(opp, "Test Widget")

        d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = gremlins,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(widget))
            )
        )
        d.bothPass()
        d.state.getEntity(widget)?.has<TappedComponent>() shouldBe true

        // The opponent's untap step (turn 1's other half) leaves the widget tapped — DOESNT_UNTAP —
        // so it never pauses. The first untap step that pauses is OURS (Gremlins is MAY_NOT_UNTAP).
        d.passPriorityUntil(Step.UNTAP)
        d.activePlayer shouldBe me
        // Still locked the whole time: the widget never untapped on the opponent's untap step.
        d.state.getEntity(widget)?.has<TappedComponent>() shouldBe true

        // Untap Gremlins: the WhileSourceTapped grant drops off the widget.
        d.submitCardSelection(me, emptyList())
        d.state.getEntity(gremlins)?.has<TappedComponent>() shouldBe false
        projector.project(d.state).hasKeyword(widget, AbilityFlag.DOESNT_UNTAP) shouldBe false

        // On the opponent's following untap step the widget untaps normally.
        d.passPriorityUntil(Step.UPKEEP)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.passPriorityUntil(Step.UPKEEP)
        d.activePlayer shouldBe opp
        d.state.getEntity(widget)?.has<TappedComponent>() shouldBe false
    }
})
