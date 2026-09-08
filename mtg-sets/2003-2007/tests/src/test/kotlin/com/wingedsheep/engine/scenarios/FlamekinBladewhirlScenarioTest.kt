package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.FlamekinBladewhirl
import com.wingedsheep.mtg.sets.definitions.lrw.cards.FlamekinSpitfire
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
 * Flamekin Bladewhirl (LRW #165) — {R} Creature — Elemental Warrior 2/1.
 *
 * "As an additional cost to cast this spell, reveal an Elemental card from your hand or pay {3}."
 *
 * A vanilla body behind a tribal cost, so the two prices are the card. The reveal-or-pay machinery
 * is proved in `RevealFromHandAdditionalCostTest`; this pins the Elemental filter and that the
 * revealed Elemental is untouched (CR 701.20b).
 */
class FlamekinBladewhirlScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(FlamekinBladewhirl, FlamekinSpitfire))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.castsOf(cardId: EntityId) =
        LegalActionEnumerator.create(cardRegistry)
            .enumerate(state, player1)
            .filter { (it.action as? CastSpell)?.cardId == cardId }

    fun GameTestDriver.tappedMountains(): Int = state.getZone(player1, Zone.BATTLEFIELD).count { id ->
        state.getEntity(id)?.get<CardComponent>()?.name == "Mountain" &&
            state.getEntity(id)?.has<TappedComponent>() == true
    }

    test("an Elemental card in hand casts it for a single Mountain and stays in hand") {
        val d = driver()
        d.putLandOnBattlefield(d.player1, "Mountain")
        val bladewhirl = d.putCardInHand(d.player1, "Flamekin Bladewhirl")
        val elemental = d.putCardInHand(d.player1, "Flamekin Spitfire")

        val revealLeg = d.castsOf(bladewhirl).firstOrNull { it.additionalCostInfo?.costType == "RevealCard" }
        revealLeg.shouldNotBeNull()
        revealLeg.additionalCostInfo!!.validRevealTargets shouldBe listOf(elemental)

        d.submitSuccess(
            CastSpell(
                playerId = d.player1,
                cardId = bladewhirl,
                additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(elemental)),
            )
        )
        withClue("CR 701.20b — nothing moved") { d.getHand(d.player1) shouldContain elemental }
        withClue("the base {R} alone") { d.tappedMountains() shouldBe 1 }

        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }
        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(bladewhirl).shouldBeTrue()
    }

    test("with no Elemental in hand it costs {3}{R}") {
        val d = driver()
        repeat(4) { d.putLandOnBattlefield(d.player1, "Mountain") }
        val bladewhirl = d.putCardInHand(d.player1, "Flamekin Bladewhirl")

        val casts = d.castsOf(bladewhirl)
        casts.size shouldBe 1
        casts.single().additionalCostInfo?.costType shouldBe null

        d.submitSuccess(casts.single().action)
        withClue("{R} + {3} taps all four Mountains") { d.tappedMountains() shouldBe 4 }

        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }
        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(bladewhirl).shouldBeTrue()
    }
})
