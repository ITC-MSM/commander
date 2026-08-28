package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.MaskedAdmirers
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Masked Admirers (LRW #230) — {2}{G}{G} Creature — Elf Shaman 3/2
 *
 *   When this creature enters, draw a card.
 *   Whenever you cast a creature spell, you may pay {G}{G}. If you do, return this card from your
 *   graveyard to your hand.
 *
 * The recursion ability functions from the *graveyard* (CR 113.6b). That is the half a card like
 * this silently loses: index the trigger on the battlefield only and it can never fire, because a
 * Masked Admirers on the battlefield has nothing to return. So the tests here bracket the zone —
 * it fires from the graveyard, and it does not fire from the battlefield.
 */
class MaskedAdmirersScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + MaskedAdmirers)
        return driver
    }

    test("entering the battlefield draws a card") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val admirers = driver.putCardInHand(p1, "Masked Admirers")
        driver.giveMana(p1, Color.GREEN, 4)
        val handBefore = driver.getHandSize(p1)

        driver.castSpell(p1, admirers)
        driver.bothPass() // resolve the creature
        driver.bothPass() // resolve the ETB trigger

        withClue("one card left the hand (the Admirers) and one was drawn") {
            driver.getHandSize(p1) shouldBe handBefore
        }
    }

    test("casting a creature spell and paying {G}{G} returns it from the graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCardInGraveyard(p1, "Masked Admirers")

        val bear = driver.putCardInHand(p1, "Centaur Courser")
        driver.giveMana(p1, Color.GREEN, 5)
        driver.castSpell(p1, bear)
        driver.bothPass() // the trigger sits above the creature spell, so it resolves first

        driver.submitYesNo(p1, true)
        driver.bothPass()

        withClue("the Admirers left the graveyard") {
            driver.getGraveyardCardNames(p1).contains("Masked Admirers") shouldBe false
        }
        driver.getHand(p1).any { driver.getCardName(it) == "Masked Admirers" } shouldBe true
    }

    test("declining the payment leaves it in the graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCardInGraveyard(p1, "Masked Admirers")

        val bear = driver.putCardInHand(p1, "Centaur Courser")
        driver.giveMana(p1, Color.GREEN, 5)
        driver.castSpell(p1, bear)
        driver.bothPass() // resolve the trigger

        driver.submitYesNo(p1, false)
        driver.bothPass()

        driver.getGraveyardCardNames(p1).contains("Masked Admirers") shouldBe true
    }

    test("a noncreature spell does not offer the payment") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCardInGraveyard(p1, "Masked Admirers")

        val bolt = driver.putCardInHand(p1, "Lightning Bolt")
        driver.giveMana(p1, Color.RED, 1)
        driver.giveMana(p1, Color.GREEN, 2)
        driver.castSpell(p1, bolt, listOf(driver.getOpponent(p1)))

        withClue("only the Bolt is on the stack — 'a creature spell' held") {
            driver.stackSize shouldBe 1
        }
    }

    test("the trigger does not fire while the Admirers is on the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(p1, "Masked Admirers")

        val bear = driver.putCardInHand(p1, "Centaur Courser")
        driver.giveMana(p1, Color.GREEN, 5)
        driver.castSpell(p1, bear)

        withClue("`activeZones = [GRAVEYARD]` switches the ability off on the battlefield") {
            driver.stackSize shouldBe 1
        }
    }
})
