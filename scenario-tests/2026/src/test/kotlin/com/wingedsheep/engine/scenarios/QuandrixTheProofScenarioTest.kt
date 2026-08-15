package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.QuandrixTheProof
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Quandrix, the Proof {4}{G}{U} — Flying, trample, Cascade, and "Instant and sorcery spells you
 * cast from your hand have cascade." Both halves are cast-trigger cascade abilities (CR 702.85a)
 * feeding the shared cascade executor; a cascade hit pauses with a "cast for free?" decision.
 */
class QuandrixTheProofScenarioTest : FunSpec({

    // A {4} instant (MV 4) so Lightning Bolt (MV 1) is a legal cascade hit.
    val BigBlast = card("Big Blast") {
        manaCost = "{4}"
        typeLine = "Instant"
        spell { effect = Effects.GainLife(1) }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(QuandrixTheProof)
        driver.registerCard(BigBlast)
        return driver
    }

    test("Quandrix's own cascade fires when cast and finds a cheaper nonland card") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        // Cascade walks from the top: a land, then Lightning Bolt (MV 1 < Quandrix's MV 6) as the hit.
        driver.putCardOnTopOfLibrary(me, "Lightning Bolt")
        driver.putCardOnTopOfLibrary(me, "Forest")

        val quandrix = driver.putCardInHand(me, "Quandrix, the Proof")
        driver.giveMana(me, Color.GREEN, 1)
        driver.giveMana(me, Color.BLUE, 1)
        driver.giveColorlessMana(me, 4)
        driver.submit(
            CastSpell(playerId = me, cardId = quandrix, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass() // cascade trigger resolves -> finds Lightning Bolt -> may-cast pause

        driver.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
    }

    test("an instant cast from hand gets cascade while Quandrix is in play") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.putCreatureOnBattlefield(me, "Quandrix, the Proof")

        // Cascade walks from the top: a land, then Lightning Bolt (MV 1 < Big Blast's MV 4).
        driver.putCardOnTopOfLibrary(me, "Lightning Bolt")
        driver.putCardOnTopOfLibrary(me, "Forest")

        val blast = driver.putCardInHand(me, "Big Blast")
        driver.giveColorlessMana(me, 4)
        driver.submit(
            CastSpell(playerId = me, cardId = blast, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass() // granted cascade trigger resolves -> may-cast pause

        driver.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
    }

    test("a creature spell from hand does NOT get cascade (filter is instant/sorcery)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.putCreatureOnBattlefield(me, "Quandrix, the Proof")
        driver.putCardOnTopOfLibrary(me, "Lightning Bolt")
        driver.putCardOnTopOfLibrary(me, "Forest")

        // Centaur Courser ({2}{G}) is a creature spell — not instant/sorcery, so no cascade.
        val courser = driver.putCardInHand(me, "Centaur Courser")
        driver.giveMana(me, Color.GREEN, 1)
        driver.giveColorlessMana(me, 2)
        driver.submit(
            CastSpell(playerId = me, cardId = courser, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true
        driver.bothPass()

        // No cascade decision — the creature simply resolves.
        driver.isPaused shouldBe false
    }
})
