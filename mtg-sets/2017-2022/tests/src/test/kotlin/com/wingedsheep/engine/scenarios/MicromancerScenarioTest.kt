package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dmu.cards.Micromancer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Micromancer — {3}{U} 3/3
 *
 * "When this creature enters, you may search your library for an instant or sorcery card with
 * mana value 1, reveal it, put it into your hand, then shuffle."
 *
 * Proves the ETB tutor only offers MV-1 instants/sorceries (a MV-2 sorcery and a creature seeded in
 * the library are not legal choices), that accepting fetches the card to hand, and that declining
 * the "may" leaves the hand unchanged.
 */
class MicromancerScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(Micromancer))
        return driver
    }

    test("ETB tutors an MV-1 instant/sorcery to hand; non-matching cards are not offered") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!

        // Seed the library: one legal choice (MV-1 instant) plus two illegal ones.
        val bolt = driver.putCardOnTopOfLibrary(me, "Lightning Bolt") // {R} instant, MV 1 — matches
        driver.putCardOnTopOfLibrary(me, "Doom Blade")                // {1}{B} sorcery, MV 2 — no match
        driver.putCardOnTopOfLibrary(me, "Centaur Courser")           // creature — no match

        val micromancer = driver.putCardInHand(me, "Micromancer")
        driver.giveMana(me, Color.BLUE, 4)
        driver.castSpell(me, micromancer).isSuccess shouldBe true

        // Resolve the creature, then its ETB "may" trigger: accept the yes/no, capture the search.
        var searchDecision: SelectCardsDecision? = null
        var safety = 0
        while (safety < 40) {
            val pd = driver.pendingDecision
            when {
                pd is YesNoDecision -> driver.submitYesNo(pd.playerId, true)
                pd is SelectCardsDecision -> { searchDecision = pd; break }
                driver.stackSize > 0 -> driver.bothPass()
                else -> break
            }
            safety++
        }

        val decision = searchDecision
        (decision != null) shouldBe true
        // Only the MV-1 instant is a legal choice.
        decision!!.options.size shouldBe 1
        decision.options.first() shouldBe bolt

        driver.submitCardSelection(me, listOf(bolt))
        safety = 0
        while (driver.stackSize > 0 && safety < 20) {
            driver.bothPass()
            safety++
        }

        driver.findCardInHand(me, "Lightning Bolt") shouldNotBe null
    }

    test("declining the optional search leaves the hand unchanged") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        driver.putCardOnTopOfLibrary(me, "Lightning Bolt")

        val micromancer = driver.putCardInHand(me, "Micromancer")
        driver.giveMana(me, Color.BLUE, 4)
        driver.castSpell(me, micromancer).isSuccess shouldBe true

        var declined = false
        var safety = 0
        while (safety < 40) {
            val pd = driver.pendingDecision
            when {
                pd is YesNoDecision -> { driver.submitYesNo(pd.playerId, false); declined = true }
                pd is SelectCardsDecision ->
                    throw AssertionError("Declining the may should not present a search decision")
                driver.stackSize > 0 -> driver.bothPass()
                else -> break
            }
            safety++
        }

        declined shouldBe true
        driver.findCardInHand(me, "Lightning Bolt") shouldBe null
    }
})
