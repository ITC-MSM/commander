package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Meteor Golem (M19 #241) — {7} 3/3 Artifact Creature — Golem, "When this creature enters, destroy
 * target nonland permanent **an opponent controls**."
 *
 * The controller clause is the point. The card shipped without it — a generated render that dropped
 * "an opponent controls" and left the filter at any nonland permanent — so the golem could be
 * pointed at its own controller's board. Found by the Argentum Assay differential gate, which read
 * the printed sentence and diffed the reading against the committed definition.
 *
 * Both halves are asserted, because only the second one would have failed before the fix: an
 * opponent's permanent is destroyed, and the controller's own permanents are **not offered** as
 * legal targets in the first place (CR 115.4 — an illegal target can't be chosen).
 */
class MeteorGolemScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Forest" to 30))
        return d
    }

    /**
     * The golem has to be **cast**: putting a permanent straight onto the battlefield emits no
     * zone-change event, so no ETB trigger fires and there is nothing to target.
     */
    fun castGolem(d: GameTestDriver) {
        val you = d.player1
        val golem = d.putCardInHand(you, "Meteor Golem")
        repeat(7) { d.putLandOnBattlefield(you, "Forest") }
        d.submit(
            CastSpell(playerId = you, cardId = golem, paymentStrategy = PaymentStrategy.AutoPay),
        ).isSuccess shouldBe true
        d.bothPass() // resolve the golem; its ETB trigger goes on the stack and wants a target
    }

    test("the ETB destroys a nonland permanent an opponent controls") {
        val d = driver()
        val you = d.player1
        val opponent = d.player2
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val theirs = d.putCreatureOnBattlefield(opponent, "Centaur Courser")
        castGolem(d)

        val decision = d.pendingDecision.shouldNotBeNull() as ChooseTargetsDecision
        decision.legalTargets.getValue(0) shouldContain theirs

        d.submitTargetSelection(you, listOf(theirs))
        while (d.stackSize > 0) d.bothPass()

        d.findPermanent(opponent, "Centaur Courser").shouldBeNull()
    }

    // The regression the gate found. With the printed clause dropped, the controller's own
    // permanents were legal targets and the golem could shoot its own side of the board.
    test("a permanent you control is not a legal target") {
        val d = driver()
        val you = d.player1
        val opponent = d.player2
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val mine = d.putCreatureOnBattlefield(you, "Centaur Courser")
        val theirs = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        castGolem(d)

        val decision = d.pendingDecision.shouldNotBeNull() as ChooseTargetsDecision
        val legal = decision.legalTargets.getValue(0)

        legal shouldContain theirs
        legal shouldNotContain mine
        // The golem itself is a nonland permanent too, and it is on the controller's side.
        legal.none { d.state.getEntity(it)?.get<CardComponent>()?.name == "Meteor Golem" } shouldBe true
    }
})
