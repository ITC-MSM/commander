package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.GoldmeadowStalwart
import com.wingedsheep.mtg.sets.definitions.lrw.cards.KithkinHealer
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
 * Goldmeadow Stalwart (LRW #18) — {W} Creature — Kithkin Soldier 2/2.
 *
 * "As an additional cost to cast this spell, reveal a Kithkin card from your hand or pay {3}."
 *
 * The whole card is its cost, so the two legs are the whole test: a 2/2 for one Plains out of a
 * Kithkin hand, and a 2/2 for four Plains out of any other. The reveal-or-pay machinery is proved
 * in `RevealFromHandAdditionalCostTest`; this pins the Kithkin filter and the two prices.
 */
class GoldmeadowStalwartScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(GoldmeadowStalwart, KithkinHealer))
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.castsOf(cardId: EntityId) =
        LegalActionEnumerator.create(cardRegistry)
            .enumerate(state, player1)
            .filter { (it.action as? CastSpell)?.cardId == cardId }

    fun GameTestDriver.tappedPlains(): Int = state.getZone(player1, Zone.BATTLEFIELD).count { id ->
        state.getEntity(id)?.get<CardComponent>()?.name == "Plains" &&
            state.getEntity(id)?.has<TappedComponent>() == true
    }

    test("a Kithkin card in hand makes it a 2/2 for {W}, and that Kithkin stays in hand") {
        val d = driver()
        // A single Plains: the {3} leg is out of reach, so this proves the reveal leg was taken.
        d.putLandOnBattlefield(d.player1, "Plains")
        val stalwart = d.putCardInHand(d.player1, "Goldmeadow Stalwart")
        val kithkin = d.putCardInHand(d.player1, "Kithkin Healer")

        val revealLeg = d.castsOf(stalwart).firstOrNull { it.additionalCostInfo?.costType == "RevealCard" }
        revealLeg.shouldNotBeNull()
        revealLeg.additionalCostInfo!!.validRevealTargets shouldBe listOf(kithkin)

        d.submitSuccess(
            CastSpell(
                playerId = d.player1,
                cardId = stalwart,
                additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(kithkin)),
            )
        )
        withClue("CR 701.20b — revealing moved nothing") { d.getHand(d.player1) shouldContain kithkin }
        withClue("one Plains — the base {W} alone") { d.tappedPlains() shouldBe 1 }

        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }
        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(stalwart).shouldBeTrue()
    }

    test("with no Kithkin in hand it costs {3}{W} instead") {
        val d = driver()
        repeat(4) { d.putLandOnBattlefield(d.player1, "Plains") }
        val stalwart = d.putCardInHand(d.player1, "Goldmeadow Stalwart")

        val casts = d.castsOf(stalwart)
        casts.size shouldBe 1
        casts.single().additionalCostInfo?.costType shouldBe null

        d.submitSuccess(casts.single().action)
        withClue("{W} + {3} taps all four Plains") { d.tappedPlains() shouldBe 4 }

        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }
        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(stalwart).shouldBeTrue()
    }

    test("one Plains and no Kithkin means it can't be cast at all") {
        val d = driver()
        d.putLandOnBattlefield(d.player1, "Plains")
        val stalwart = d.putCardInHand(d.player1, "Goldmeadow Stalwart")

        withClue("the pay path needs {3}{W}; the reveal path has nothing to reveal") {
            d.castsOf(stalwart).isEmpty().shouldBeTrue()
        }
    }
})
