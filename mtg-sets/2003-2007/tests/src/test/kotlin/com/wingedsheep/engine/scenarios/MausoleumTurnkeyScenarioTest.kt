package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.MausoleumTurnkey
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Mausoleum Turnkey (RAV #94) — "When this creature enters, return target creature card of an
 * opponent's choice from your graveyard to your hand."
 *
 * `TargetChooser.Opponent` already existed but was honored on *activated* abilities only (Cuombajj
 * Witches); `CardLinter` failed any card that put it on a triggered one, and
 * `TriggerProcessor.resolveTargetChooser` mapped it to the controller on purpose. This is the
 * printed triggered use. What these tests pin:
 *
 * - the target decision is raised for the **opponent**, not the controller;
 * - the chooser is **orthogonal to legality** — the legal pool is still *your* graveyard, and the
 *   card comes back to *your* hand, so an opponent cannot fish out of their own graveyard;
 * - a graveyard with no creature card leaves the trigger with no legal target, and it never reaches
 *   the stack (CR 603.3d) — nobody is asked anything;
 * - in multiplayer the controller first picks *which* opponent decides, mirroring the
 *   activated-ability path.
 */
class MausoleumTurnkeyScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + MausoleumTurnkey)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("the enters trigger routes its target decision to the opponent") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        val lions = d.putCardInGraveyard(me, "Savannah Lions")
        val courser = d.putCardInGraveyard(me, "Centaur Courser")
        val theirs = d.putCardInGraveyard(opp, "Phantom Warrior")

        val card = d.putCardInHand(me, "Mausoleum Turnkey")
        d.giveColorlessMana(me, 3)
        d.giveMana(me, Color.BLACK, 1)
        d.castSpell(me, card).error shouldBe null
        var guard = 0
        while (d.pendingDecision == null && d.stackSize > 0 && guard++ < 10) d.bothPass()

        val decision = d.pendingDecision
        withClue("a target decision was raised: $decision") {
            (decision is ChooseTargetsDecision) shouldBe true
        }
        decision as ChooseTargetsDecision
        withClue("…and the opponent is the one answering it") { decision.playerId shouldBe opp }
        withClue("the pool is the controller's graveyard, not the chooser's") {
            decision.legalTargets[0]?.toSet() shouldBe setOf(lions, courser)
        }
        withClue("the opponent's own creature card is not on offer") {
            decision.legalTargets[0]?.contains(theirs) shouldBe false
        }

        // The opponent picks the weaker one, which is the whole point of the drawback.
        d.submitDecision(
            opp,
            TargetsResponse(decision.id, mapOf(0 to listOf(lions)))
        ).error shouldBe null
        guard = 0
        while (d.stackSize > 0 && guard++ < 10) d.bothPass()

        withClue("it returns to the controller's hand, not the chooser's") {
            d.findCardInHand(me, "Savannah Lions") shouldBe lions
            d.findCardInHand(opp, "Savannah Lions") shouldBe null
        }
        withClue("the card the opponent declined stays in the graveyard") {
            d.getGraveyardCardNames(me).contains("Centaur Courser") shouldBe true
        }
    }

    test("an empty graveyard leaves no legal target, so nobody is asked") {
        val d = driver()
        val me = d.player1
        // Only a non-creature card in the graveyard.
        d.putCardInGraveyard(me, "Lightning Bolt")
        d.putCardInGraveyard(d.player2, "Phantom Warrior")

        val card = d.putCardInHand(me, "Mausoleum Turnkey")
        d.giveColorlessMana(me, 3)
        d.giveMana(me, Color.BLACK, 1)
        d.castSpell(me, card).error shouldBe null
        var guard = 0
        while (d.stackSize > 0 && guard++ < 10) {
            withClue("no decision should be raised at all") { d.pendingDecision shouldBe null }
            d.bothPass()
        }

        withClue("the Turnkey still resolved as a creature") {
            d.findPermanent(me, "Mausoleum Turnkey") shouldNotBe null
        }
    }

    test("with two opponents the controller picks which one decides") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + MausoleumTurnkey)
        val players = d.initMultiplayer(
            decks = List(3) { Deck.of("Swamp" to 40) },
            skipMulligans = true,
            startingPlayer = 0
        )
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = players[0]
        val second = players[2]

        val lions = d.putCardInGraveyard(me, "Savannah Lions")
        d.putCardInGraveyard(me, "Centaur Courser")

        val card = d.putCardInHand(me, "Mausoleum Turnkey")
        d.giveColorlessMana(me, 3)
        d.giveMana(me, Color.BLACK, 1)
        d.castSpell(me, card).error shouldBe null
        var guard = 0
        while (d.pendingDecision == null && d.stackSize > 0 && guard++ < 20) d.passPriority(d.state.priorityPlayerId!!)

        val chooserPick = d.pendingDecision
        withClue("the controller is asked which opponent decides: $chooserPick") {
            (chooserPick is ChooseOptionDecision) shouldBe true
            (chooserPick as ChooseOptionDecision).playerId shouldBe me
            chooserPick.options.size shouldBe 2
        }
        // Pick the second opponent — proving the answer is honored rather than defaulted.
        d.submitDecision(
            me,
            OptionChosenResponse((chooserPick as ChooseOptionDecision).id, 1)
        ).error shouldBe null

        val targets = d.pendingDecision
        withClue("the chosen opponent now answers the target decision: $targets") {
            (targets is ChooseTargetsDecision) shouldBe true
            (targets as ChooseTargetsDecision).playerId shouldBe second
        }
        d.submitDecision(
            second,
            TargetsResponse(
                (targets as ChooseTargetsDecision).id,
                mapOf(0 to listOf(lions))
            )
        ).error shouldBe null
    }
})
