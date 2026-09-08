package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.EyeblightsEnding
import com.wingedsheep.mtg.sets.definitions.lrw.cards.WarrenScourgeElf
import com.wingedsheep.mtg.sets.definitions.lrw.cards.WrensRunVanquisher
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
 * Wren's Run Vanquisher (LRW #245) — {1}{G} Creature — Elf Warrior 3/3 with deathtouch.
 *
 * "As an additional cost to cast this spell, reveal an Elf card from your hand or pay {3}.
 *  Deathtouch"
 *
 * The archetype of Lorwyn's reveal-or-pay cycle. Pins the Elf filter, that a *Kindred noncreature*
 * Elf card pays it too (Eyeblight's Ending — "an Elf card", not "an Elf creature card"), and that
 * the body lands with deathtouch. The cost machinery is proved in
 * `RevealFromHandAdditionalCostTest`.
 */
class WrensRunVanquisherScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(WrensRunVanquisher, WarrenScourgeElf, EyeblightsEnding))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.castsOf(cardId: EntityId) =
        LegalActionEnumerator.create(cardRegistry)
            .enumerate(state, player1)
            .filter { (it.action as? CastSpell)?.cardId == cardId }

    test("revealing an Elf creature card casts it for {1}{G} with deathtouch") {
        val d = driver()
        // Exactly {1}{G}: the {3} leg is unaffordable, so a successful cast is the reveal leg.
        repeat(2) { d.putLandOnBattlefield(d.player1, "Forest") }
        val vanquisher = d.putCardInHand(d.player1, "Wren's Run Vanquisher")
        val elf = d.putCardInHand(d.player1, "Warren-Scourge Elf")

        val revealLeg = d.castsOf(vanquisher).firstOrNull { it.additionalCostInfo?.costType == "RevealCard" }
        revealLeg.shouldNotBeNull()
        revealLeg.additionalCostInfo!!.validRevealTargets shouldBe listOf(elf)

        d.submitSuccess(
            CastSpell(
                playerId = d.player1,
                cardId = vanquisher,
                additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(elf)),
            )
        )
        withClue("CR 701.20b — the Elf shown is still in hand") { d.getHand(d.player1) shouldContain elf }
        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(vanquisher).shouldBeTrue()
        withClue("deathtouch is on the resolved permanent") {
            d.state.projectedState.hasKeyword(vanquisher, Keyword.DEATHTOUCH).shouldBeTrue()
        }
    }

    test("a Kindred noncreature Elf card pays it — the cost asks for an Elf card, not an Elf creature") {
        val d = driver()
        repeat(2) { d.putLandOnBattlefield(d.player1, "Forest") }
        val vanquisher = d.putCardInHand(d.player1, "Wren's Run Vanquisher")
        // Eyeblight's Ending is a Kindred Instant — Elf: an Elf card that is never a creature.
        val kindredElf = d.putCardInHand(d.player1, "Eyeblight's Ending")

        val revealLeg = d.castsOf(vanquisher).firstOrNull { it.additionalCostInfo?.costType == "RevealCard" }
        revealLeg.shouldNotBeNull()
        revealLeg.additionalCostInfo!!.validRevealTargets shouldBe listOf(kindredElf)

        d.submitSuccess(
            CastSpell(
                playerId = d.player1,
                cardId = vanquisher,
                additionalCostPayment = AdditionalCostPayment(revealedCards = listOf(kindredElf)),
            )
        )
        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }
        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(vanquisher).shouldBeTrue()
    }

    test("with no Elf in hand it costs {3}{1}{G}") {
        val d = driver()
        repeat(5) { d.putLandOnBattlefield(d.player1, "Forest") }
        val vanquisher = d.putCardInHand(d.player1, "Wren's Run Vanquisher")

        val casts = d.castsOf(vanquisher)
        casts.size shouldBe 1
        casts.single().additionalCostInfo?.costType shouldBe null

        d.submitSuccess(casts.single().action)
        repeat(4) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }
        d.state.getZone(d.player1, Zone.BATTLEFIELD).contains(vanquisher).shouldBeTrue()
    }

    test("four Forests and no Elf is not enough — the spell isn't castable") {
        val d = driver()
        repeat(4) { d.putLandOnBattlefield(d.player1, "Forest") }
        val vanquisher = d.putCardInHand(d.player1, "Wren's Run Vanquisher")

        withClue("the pay path needs five mana; the reveal path has nothing to reveal") {
            d.castsOf(vanquisher).isEmpty().shouldBeTrue()
        }
    }
})
