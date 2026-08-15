package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Subway Train (SPM) — {2} Artifact — Vehicle 3/1, Crew 2.
 *
 *  "When this Vehicle enters, you may pay {G}. If you do, search your library for a basic land
 *   card, reveal it, put it into your hand, then shuffle."
 *
 * Verifies that paying {G} fetches a basic land to hand, and that declining performs no search.
 * The deck is non-basic (Grizzly Bears) so the opening hand has no Forest; a single Forest is
 * seeded into the library as the only fetchable basic land — making the hand-count delta exact.
 */
class SubwayTrainScenarioTest : FunSpec({

    fun forestsInHand(driver: GameTestDriver, me: com.wingedsheep.sdk.model.EntityId): Int =
        driver.state.getHand(me).count { driver.getCardName(it) == "Forest" }

    test("paying {G} fetches a basic land card to hand") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        // The only basic land in the library, so it's the unique fetch target.
        driver.putCardOnTopOfLibrary(me, "Forest")

        val train = driver.putCardInHand(me, "Subway Train")
        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, com.wingedsheep.sdk.core.Color.GREEN, 1)

        forestsInHand(driver, me) shouldBe 0

        driver.castSpell(me, train)
        driver.bothPass() // resolve Subway Train -> ETB trigger on stack
        driver.bothPass() // resolve ETB trigger -> optional mana payment prompt

        // "You may pay {G}" — accept; the {G} is paid from the pool.
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        driver.submitYesNo(me, true)

        // A mana-source selection may follow before the search; auto-pay if so.
        if (driver.pendingDecision is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
            driver.submitManaAutoPayOrDecline(me, autoPay = true)
        }

        // Then search the library for a basic land to put into hand.
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        val options = (driver.pendingDecision as SelectCardsDecision).options
        driver.submitCardSelection(me, listOf(options.first()))

        // The fetched Forest is now in hand.
        forestsInHand(driver, me) shouldBe 1
    }

    test("declining the {G} payment performs no search") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        driver.putCardOnTopOfLibrary(me, "Forest")

        val train = driver.putCardInHand(me, "Subway Train")
        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, com.wingedsheep.sdk.core.Color.GREEN, 1)

        forestsInHand(driver, me) shouldBe 0

        driver.castSpell(me, train)
        driver.bothPass()
        driver.bothPass()

        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        // Decline the optional payment.
        driver.submitYesNo(me, false)

        // No search decision should follow — no basic land enters the hand.
        (driver.pendingDecision is SelectCardsDecision) shouldBe false
        forestsInHand(driver, me) shouldBe 0
    }
})
