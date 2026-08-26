package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.LibraryShuffledEvent
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.isd.cards.SnapcasterMage
import com.wingedsheep.mtg.sets.definitions.mbs.cards.BlueSunsZenith
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Blue Sun's Zenith — "Target player draws X cards. Shuffle Blue Sun's Zenith into its owner's
 * library."
 *
 * The cycle's only instant, which is what makes it the card that meets **Snapcaster Mage**: the
 * printed "shuffle this into its owner's library" clause and flashback both want to say where the
 * card goes as it resolves, and flashback wins. CR 702.34a is worded "exile this card instead of
 * putting it **anywhere else** any time it would leave the stack" — not "instead of putting it into
 * its owner's graveyard", which is how the cast-this-way rider, rebound, Adventure and Omen are all
 * worded and why the printed clause beats *those*. Getting this backwards turns Snapcaster plus this
 * card into a repeatable draw-X loop, so it is worth a card-level test and not only the engine one
 * (`SelfShuffleIntoLibraryOnResolveScenarioTest`).
 */
class BlueSunsZenithScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BlueSunsZenith)
        driver.registerCard(SnapcasterMage)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
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

    test("target player draws X, then the spell shuffles itself into its owner's library") {
        val driver = newDriver()
        val you = driver.player1
        val handBefore = driver.getHand(you).size
        val librarySizeBefore = driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).size

        val zenith = driver.putCardInHand(you, "Blue Sun's Zenith")
        driver.giveMana(you, Color.BLUE, 8)
        driver.submit(
            CastSpell(
                you, zenith,
                targets = listOf(ChosenTarget.Player(you)),
                xValue = 3,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        driver.settle()

        withClue("three cards drawn — the Zenith itself left the hand to be cast") {
            driver.getHand(you).size shouldBe handBefore + 3 - 1 + 1
        }
        withClue("three cards left the library and the Zenith came back into it") {
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).contains(zenith) shouldBe true
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).size shouldBe librarySizeBefore - 3 + 1
            driver.getGraveyardCardNames(you).contains("Blue Sun's Zenith") shouldBe false
        }
    }

    test("it can deck an opponent — the target is a player, not 'you'") {
        val driver = newDriver()
        val you = driver.player1
        val opponent = driver.getOpponent(you)
        val opponentHandBefore = driver.getHand(opponent).size

        val zenith = driver.putCardInHand(you, "Blue Sun's Zenith")
        driver.giveMana(you, Color.BLUE, 8)
        driver.submit(
            CastSpell(
                you, zenith,
                targets = listOf(ChosenTarget.Player(opponent)),
                xValue = 2,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        driver.settle()

        withClue("the opponent drew, and the card went back into *its owner's* library — yours") {
            driver.getHand(opponent).size shouldBe opponentHandBefore + 2
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).contains(zenith) shouldBe true
        }
    }

    test("flashbacked off Snapcaster Mage it is exiled, not shuffled in — CR 702.34a") {
        val driver = newDriver()
        val you = driver.player1
        val zenith = driver.putCardInGraveyard(you, "Blue Sun's Zenith")

        // Cast Snapcaster Mage so its enters trigger actually fires, and point it at the Zenith.
        val snapcaster = driver.putCardInHand(you, "Snapcaster Mage")
        driver.giveMana(you, Color.BLUE, 2)
        driver.submit(
            CastSpell(you, snapcaster, paymentStrategy = PaymentStrategy.FromPool)
        ).error shouldBe null
        driver.bothPass()
        if (driver.isPaused) driver.submitTargetSelection(you, listOf(zenith))
        driver.settle()

        withClue("the grant landed on the card in the graveyard") {
            driver.state.grantedKeywordAbilities.any { it.entityId == zenith } shouldBe true
        }

        val shufflesBefore = driver.events.count { it is LibraryShuffledEvent && it.playerId == you }
        val librarySizeBefore = driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).size

        // Flashback cost is the card's mana cost, {X}{U}{U}{U}; X = 1 makes that four mana.
        driver.giveMana(you, Color.BLUE, 4)
        driver.submit(
            CastSpell(
                you, zenith,
                targets = listOf(ChosenTarget.Player(you)),
                xValue = 1,
                useAlternativeCost = true,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        driver.settle()

        withClue("flashback replaces 'anywhere else', which covers the library the printed clause " +
            "asks for — otherwise this is an infinite Snapcaster loop") {
            driver.getExile(you).contains(zenith) shouldBe true
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).contains(zenith) shouldBe false
        }
        withClue("one card drawn, and no shuffle at all — the card never reached the library") {
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).size shouldBe librarySizeBefore - 1
            driver.events.count { it is LibraryShuffledEvent && it.playerId == you } shouldBe
                shufflesBefore
        }
    }
})
