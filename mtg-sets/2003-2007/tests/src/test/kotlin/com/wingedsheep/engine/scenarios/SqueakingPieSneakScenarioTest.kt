package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.MadAuntie
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SqueakingPieSneak
import com.wingedsheep.sdk.core.Keyword
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
 * Squeaking Pie Sneak (LRW #142) — {1}{B} Creature — Goblin Rogue 2/2 with fear.
 *
 * "As an additional cost to cast this spell, reveal a Goblin card from your hand or pay {3}.
 *  Fear"
 *
 * Pins the Goblin filter on the reveal-or-pay cost and that the creature actually lands with fear
 * — the two things that make this card what it is. The cost machinery itself is proved in
 * `RevealFromHandAdditionalCostTest`.
 */
class SqueakingPieSneakScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(SqueakingPieSneak, MadAuntie))
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.castsOf(cardId: EntityId) =
        LegalActionEnumerator.create(cardRegistry)
            .enumerate(state, player1)
            .filter { (it.action as? CastSpell)?.cardId == cardId }

    test("revealing a Goblin card casts it for {1}{B}, and it enters with fear") {
        val d = driver()
        // Exactly {1}{B} — the {3} leg is unaffordable, so a successful cast is the reveal leg.
        repeat(2) { d.putLandOnBattlefield(d.player1, "Swamp") }
        val sneak = d.putCardInHand(d.player1, "Squeaking Pie Sneak")
        val goblin = d.putCardInHand(d.player1, "Mad Auntie")

        val revealLeg = d.castsOf(sneak).firstOrNull { it.additionalCostInfo?.costType == "RevealCard" }
        revealLeg.shouldNotBeNull()
        revealLeg.additionalCostInfo!!.validRevealTargets shouldBe listOf(goblin)

        d.submitSuccess(
            CastSpell(
                playerId = d.player1,
                cardId = sneak,
                additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(goblin)),
            )
        )
        withClue("CR 701.20b — the Goblin shown is still in hand") {
            d.getHand(d.player1) shouldContain goblin
        }
        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(sneak).shouldBeTrue()
        withClue("fear is on the resolved permanent") {
            d.state.projectedState.hasKeyword(sneak, Keyword.FEAR).shouldBeTrue()
        }
    }

    test("with no Goblin in hand only the {3} leg is offered") {
        val d = driver()
        repeat(5) { d.putLandOnBattlefield(d.player1, "Swamp") }
        val sneak = d.putCardInHand(d.player1, "Squeaking Pie Sneak")

        val casts = d.castsOf(sneak)
        casts.size shouldBe 1
        casts.single().additionalCostInfo?.costType shouldBe null
        d.submitSuccess(casts.single().action)

        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }
        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(sneak).shouldBeTrue()
    }
})
