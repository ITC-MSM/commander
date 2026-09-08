package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.MerrowReejerey
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SilvergillAdept
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Silvergill Adept (LRW #86) — {1}{U} Creature — Merfolk Wizard 2/1.
 *
 * "As an additional cost to cast this spell, reveal a Merfolk card from your hand or pay {3}.
 *  When this creature enters, draw a card."
 *
 * The reveal-or-pay cost itself is proved in `RevealFromHandAdditionalCostTest`; what this pins is
 * that *this* card is wired to it with a Merfolk filter, that the revealed Merfolk stays in hand
 * (CR 701.20b), and that the ETB draw still fires on the cheap leg — the whole reason the card is
 * played.
 */
class SilvergillAdeptScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(SilvergillAdept, MerrowReejerey))
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.castsOf(cardId: EntityId) =
        LegalActionEnumerator.create(cardRegistry)
            .enumerate(state, player1)
            .filter { (it.action as? CastSpell)?.cardId == cardId }

    fun GameTestDriver.tappedIslands(): Int = state.getZone(player1, Zone.BATTLEFIELD).count { id ->
        state.getEntity(id)?.get<CardComponent>()?.name == "Island" &&
            state.getEntity(id)?.has<TappedComponent>() == true
    }

    test("revealing a Merfolk card pays the cost, the card stays in hand, and the ETB draw fires") {
        val d = driver()
        // Exactly the base {1}{U}: no room for the {3}, so only the reveal leg is affordable.
        repeat(2) { d.putLandOnBattlefield(d.player1, "Island") }
        val adept = d.putCardInHand(d.player1, "Silvergill Adept")
        val merfolk = d.putCardInHand(d.player1, "Merrow Reejerey")
        val handBefore = d.getHand(d.player1).size

        d.submitSuccess(
            CastSpell(
                playerId = d.player1,
                cardId = adept,
                additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(merfolk)),
            )
        )
        withClue("CR 701.20b — the revealed Merfolk never left hand") {
            d.getHand(d.player1) shouldContain merfolk
        }
        withClue("only the base {1}{U} was charged") { d.tappedIslands() shouldBe 2 }

        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(adept).shouldBeTrue()
        withClue("Adept left hand and the ETB drew one, so the hand is back where it started") {
            d.getHand(d.player1).size shouldBe handBefore
        }
    }

    test("the reveal picker offers the Merfolk in hand and not the Adept casting itself") {
        val d = driver()
        repeat(5) { d.putLandOnBattlefield(d.player1, "Island") }
        val adept = d.putCardInHand(d.player1, "Silvergill Adept")
        val merfolk = d.putCardInHand(d.player1, "Merrow Reejerey")

        val revealLeg = d.castsOf(adept).firstOrNull { it.additionalCostInfo?.costType == "RevealCard" }
        revealLeg.shouldNotBeNull()
        withClue("Silvergill Adept is a Merfolk card too, but it is on its way to the stack") {
            revealLeg.additionalCostInfo!!.validRevealTargets shouldBe listOf(merfolk)
        }
    }

    test("with no other Merfolk in hand only the {3} path is offered, and it still draws") {
        val d = driver()
        // {1}{U} + {3} = five Islands.
        repeat(5) { d.putLandOnBattlefield(d.player1, "Island") }
        val adept = d.putCardInHand(d.player1, "Silvergill Adept")
        val handBefore = d.getHand(d.player1).size

        val casts = d.castsOf(adept)
        withClue("nothing to reveal, so the leg path is declined") {
            casts.size shouldBe 1
            casts.single().additionalCostInfo?.costType shouldBe null
        }
        d.submitSuccess(casts.single().action)
        withClue("{1}{U} + {3} taps all five Islands") { d.tappedIslands() shouldBe 5 }

        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }
        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(adept).shouldBeTrue()
        d.getHand(d.player1).size shouldBe handBefore
    }
})
