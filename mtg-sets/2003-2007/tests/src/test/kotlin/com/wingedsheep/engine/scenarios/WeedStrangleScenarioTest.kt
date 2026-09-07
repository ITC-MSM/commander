package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.WeedStrangle
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Weed Strangle destroys its target and then, on a clash win, gains life equal to that creature's
 * toughness — a value the spell's own earlier step already removed from the battlefield.
 *
 * Two things these tests are actually here to prove, neither of which the snapshot net covers:
 *
 *  - The toughness is read as **projected**, not printed. Every case boosts the target with a lord
 *    so the two numbers differ; a read of base `CardComponent` stats would gain 2 instead of 3.
 *  - The stored number survives the clash's **pauses**. The clash prompts both players for a
 *    top-or-bottom decision, and the life gain sits inside the gate's `then` on the far side of
 *    both, so the store→gate propagation is exercised for real. Each player is given two clash-legal
 *    library cards so nobody's decision is auto-answered away onto the synchronous path.
 */
class WeedStrangleScenarioTest : FunSpec({
    val boulder = card("Strangle Boulder") { manaCost = "{5}"; typeLine = "Artifact"; oracleText = "" }

    fun driver() = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(WeedStrangle, boulder))
        initMirrorMatch(Deck.of("Swamp" to 40), startingPlayer = 0)
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
        test("clash win=$win — the target dies either way, life is gained only on a win") {
            val d = driver()
            // A 2/2 under a lord: projected toughness 3, printed toughness 2.
            d.putPermanentOnBattlefield(d.player2, "Glorious Anthem")
            val bears = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
            d.putCardOnTopOfLibrary(d.player1, if (win) "Strangle Boulder" else "Swamp")
            d.putCardOnTopOfLibrary(d.player2, if (win) "Swamp" else "Strangle Boulder")

            val spell = d.putCardInHand(d.player1, "Weed Strangle")
            d.giveMana(d.player1, Color.BLACK, 5)
            val startingLife = d.getLifeTotal(d.player1)

            d.castSpell(d.player1, spell, listOf(bears)).error shouldBe null
            d.bothPass().error shouldBe null
            d.resolveClashDecisions()

            d.pendingDecision shouldBe null
            d.state.getZone(ZoneKey(d.player2, Zone.GRAVEYARD)).contains(bears) shouldBe true
            d.getLifeTotal(d.player1) shouldBe startingLife + (if (win) 3 else 0)
        }
    }

    test("an indestructible target survives but still pays out its projected toughness") {
        val d = driver()
        d.putPermanentOnBattlefield(d.player2, "Glorious Anthem")
        val darksteel = d.putCreatureOnBattlefield(d.player2, "Darksteel Myr")
        d.putCardOnTopOfLibrary(d.player1, "Strangle Boulder")
        d.putCardOnTopOfLibrary(d.player2, "Swamp")

        val spell = d.putCardInHand(d.player1, "Weed Strangle")
        d.giveMana(d.player1, Color.BLACK, 5)
        val startingLife = d.getLifeTotal(d.player1)

        d.castSpell(d.player1, spell, listOf(darksteel)).error shouldBe null
        d.bothPass().error shouldBe null
        d.resolveClashDecisions()

        d.pendingDecision shouldBe null
        d.state.getZone(ZoneKey(d.player2, Zone.BATTLEFIELD)).contains(darksteel) shouldBe true
        // Darksteel Myr is 0/1 printed; the lord makes it 1/2.
        d.getLifeTotal(d.player1) shouldBe startingLife + 2
    }

    test("an illegal target fizzles the whole spell, clash included") {
        val d = driver()
        val bears = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val spell = d.putCardInHand(d.player1, "Weed Strangle")
        val bolt = d.putCardInHand(d.player1, "Lightning Bolt")
        d.giveMana(d.player1, Color.BLACK, 5)
        d.giveMana(d.player1, Color.RED, 1)
        val startingLife = d.getLifeTotal(d.player1)

        d.castSpell(d.player1, spell, listOf(bears)).error shouldBe null
        d.castSpell(d.player1, bolt, listOf(bears)).error shouldBe null
        d.bothPass().error shouldBe null
        d.bothPass().error shouldBe null

        d.pendingDecision shouldBe null
        d.state.getZone(ZoneKey(d.player2, Zone.GRAVEYARD)).contains(bears) shouldBe true
        d.state.getZone(ZoneKey(d.player1, Zone.GRAVEYARD)).contains(spell) shouldBe true
        d.getLifeTotal(d.player1) shouldBe startingLife
    }
})
