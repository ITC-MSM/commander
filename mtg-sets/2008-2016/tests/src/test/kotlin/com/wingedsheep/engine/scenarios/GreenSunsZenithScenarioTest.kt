package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fut.cards.DryadArbor
import com.wingedsheep.mtg.sets.definitions.mbs.cards.GreenSunsZenith
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Green Sun's Zenith — "Search your library for a green creature card with mana value X or less,
 * put it onto the battlefield, then shuffle. Shuffle Green Sun's Zenith into its owner's library."
 *
 * The cycle's headline card and the one that exercises `selfShuffleIntoLibrary()`'s **paused**
 * resolve path, because the library search stops for a decision before the spell leaves the stack.
 * (The vocabulary itself is covered in the engine by `SelfShuffleIntoLibraryOnResolveScenarioTest`;
 * this file is about the card.)
 *
 * The X = 0 case is the famous one: searching never compels a find (CR 701.23b), and Dryad Arbor
 * is the one green creature card with mana value 0, so `{G}` alone puts a land-creature onto the
 * battlefield.
 */
class GreenSunsZenithScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(GreenSunsZenith)
        driver.registerCard(DryadArbor)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.settle() {
        var guard = 0
        while (guard++ < 30) {
            when {
                isPaused -> autoResolveDecision()
                state.stack.isNotEmpty() -> bothPass()
                else -> break
            }
        }
    }

    test("finds a green creature within X and shuffles itself back into its owner's library") {
        val driver = createDriver()
        val you = driver.player1
        val bears = driver.putCardOnTopOfLibrary(you, "Grizzly Bears")

        val zenith = driver.putCardInHand(you, "Green Sun's Zenith")
        driver.giveMana(you, Color.GREEN, 6)
        driver.submit(
            CastSpell(you, zenith, xValue = 2, paymentStrategy = PaymentStrategy.FromPool)
        ).error shouldBe null

        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()
        driver.submitCardSelection(you, listOf(bears))
        driver.settle()

        withClue("the {G}{G} 2/2 is within X = 2 and goes straight onto the battlefield") {
            driver.state.getZone(ZoneKey(you, Zone.BATTLEFIELD)).contains(bears) shouldBe true
        }
        withClue("the spell shuffles itself in rather than going to the graveyard — and it is the " +
            "*owner's* library, which here is also the caster's") {
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).contains(zenith) shouldBe true
            driver.getGraveyardCardNames(you).contains("Green Sun's Zenith") shouldBe false
        }
    }

    test("X = 0 finds Dryad Arbor, the only green creature card with mana value 0") {
        val driver = createDriver()
        val you = driver.player1
        val arbor = driver.putCardOnTopOfLibrary(you, "Dryad Arbor")

        val zenith = driver.putCardInHand(you, "Green Sun's Zenith")
        driver.giveMana(you, Color.GREEN, 4)
        driver.submit(
            CastSpell(you, zenith, xValue = 0, paymentStrategy = PaymentStrategy.FromPool)
        ).error shouldBe null

        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()
        driver.submitCardSelection(you, listOf(arbor))
        driver.settle()

        withClue("mana value 0 is 'X or less' for X = 0, so the search may find it") {
            driver.state.getZone(ZoneKey(you, Zone.BATTLEFIELD)).contains(arbor) shouldBe true
        }
        withClue("and the Zenith still returns to the library") {
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).contains(zenith) shouldBe true
        }
    }

    test("a creature above X can't be taken, and the spell still shuffles itself in") {
        val driver = createDriver()
        val you = driver.player1
        // Grizzly Bears is {1}{G} — mana value 2, out of reach of X = 1. Nothing in the library
        // matches the filter, so there is nothing for the search to offer.
        driver.putCardOnTopOfLibrary(you, "Grizzly Bears")

        val zenith = driver.putCardInHand(you, "Green Sun's Zenith")
        driver.giveMana(you, Color.GREEN, 4)
        driver.submit(
            CastSpell(you, zenith, xValue = 1, paymentStrategy = PaymentStrategy.FromPool)
        ).error shouldBe null

        var guard = 0
        while (!driver.isPaused && driver.state.stack.isNotEmpty() && guard++ < 10) driver.bothPass()
        // CR 701.23b — a search need not find anything. With an empty candidate pool the engine
        // doesn't stop to ask; if it ever does, declining is the same answer.
        if (driver.isPaused) driver.submitCardSelection(you, emptyList())
        driver.settle()

        withClue("the out-of-range creature stayed in the library") {
            driver.findPermanent(you, "Grizzly Bears") shouldBe null
        }
        withClue("finding nothing is still a resolution, so the clause applies — this is the " +
            "case that would regress if the shuffle were wired into the search rather than into " +
            "the spell's own resolution") {
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).contains(zenith) shouldBe true
            driver.getGraveyardCardNames(you).contains("Green Sun's Zenith") shouldBe false
        }
    }
})
