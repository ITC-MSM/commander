package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.Ringskipper
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Ringskipper's dies trigger clashes and, on a win, returns *itself* from the graveyard.
 *
 * What these prove that the snapshot net can't: the ability resolves after its own source has left
 * the battlefield, so `EffectTarget.Self` has to find the card in the graveyard — across the clash's
 * two top-or-bottom pauses — and the `fromZone = GRAVEYARD` guard has to hold when the card has
 * moved on, which is exactly the printed ruling.
 */
class RingskipperScenarioTest : FunSpec({
    val boulder = card("Skipper Boulder") { manaCost = "{5}"; typeLine = "Artifact"; oracleText = "" }

    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(Ringskipper, boulder))
        initMirrorMatch(Deck.of("Island" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    /** Answer both clash top-or-bottom prompts, keeping each revealed card on top. */
    fun GameTestDriver.resolveClashDecisions() {
        repeat(2) {
            val decision = pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            submitCardSelection(decision.playerId, emptyList()).error shouldBe null
        }
    }

    for (win in listOf(true, false)) {
        test("clash win=$win — Ringskipper returns to hand only on a win") {
            val d = driver()
            val faerie = d.putCreatureOnBattlefield(d.player1, "Ringskipper")
            d.putCardOnTopOfLibrary(d.player1, if (win) "Skipper Boulder" else "Island")
            d.putCardOnTopOfLibrary(d.player2, if (win) "Island" else "Skipper Boulder")

            val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
            d.giveMana(d.player1, Color.RED, 1)
            d.castSpell(d.player1, bolt, listOf(faerie)).error shouldBe null
            d.bothPass().error shouldBe null

            // The dies trigger goes on the stack; let it resolve, then answer the clash.
            d.bothPass().error shouldBe null
            d.resolveClashDecisions()

            d.pendingDecision shouldBe null
            if (win) {
                d.state.getZone(ZoneKey(d.player1, Zone.HAND)).contains(faerie) shouldBe true
            } else {
                d.state.getZone(ZoneKey(d.player1, Zone.GRAVEYARD)).contains(faerie) shouldBe true
            }
        }
    }

    test("winning the clash does nothing if the card has already left the graveyard") {
        val d = driver()
        val faerie = d.putCreatureOnBattlefield(d.player1, "Ringskipper")
        d.putCardOnTopOfLibrary(d.player1, "Skipper Boulder")
        d.putCardOnTopOfLibrary(d.player2, "Island")

        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
        val purge = d.putCardInHand(d.player1, "Coffin Purge")
        d.giveMana(d.player1, Color.RED, 1)
        d.giveMana(d.player1, Color.BLACK, 1)
        d.castSpell(d.player1, bolt, listOf(faerie)).error shouldBe null
        d.bothPass().error shouldBe null

        // The dies trigger is on the stack; exile the card out of the graveyard in response.
        d.castSpellWithTargets(
            d.player1,
            purge,
            listOf(ChosenTarget.Card(faerie, d.player1, Zone.GRAVEYARD))
        ).error shouldBe null
        d.bothPass().error shouldBe null
        d.state.getZone(ZoneKey(d.player1, Zone.EXILE)).contains(faerie) shouldBe true

        d.bothPass().error shouldBe null
        d.resolveClashDecisions()

        d.pendingDecision shouldBe null
        d.state.getZone(ZoneKey(d.player1, Zone.EXILE)).contains(faerie) shouldBe true
        d.state.getZone(ZoneKey(d.player1, Zone.HAND)).contains(faerie) shouldBe false
    }
})
