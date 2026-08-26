package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mbs.cards.RedSunsZenith
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Red Sun's Zenith — "Red Sun's Zenith deals X damage to any target. If a creature dealt damage
 * this way would die this turn, exile it instead. Shuffle Red Sun's Zenith into its owner's
 * library."
 *
 * The cycle's one card with behaviour beyond "do a thing, then shuffle": the exile rider is a
 * CR 614 death replacement that outlives the spell, so it has to be marked before the damage and
 * has to be scoped to creatures the spell actually damaged.
 *
 * **X = 0 is the case worth a test.** This is an {X} spell, so `{R}` alone is a legal cast, and
 * CR 120.8 — "if a source would deal 0 damage, it does not deal damage at all" — means no creature
 * was "dealt damage this way". An unconditional mark would quietly exile a creature that died to
 * something else later in the turn, which is why the card gates the marker on `X > 0` rather than
 * copying Carbonize's ungated shape (Carbonize deals a fixed 3 and can't reach this).
 */
class RedSunsZenithScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(RedSunsZenith)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
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

    test("a creature killed by the damage is exiled, not put into its owner's graveyard") {
        val driver = newDriver()
        val you = driver.player1
        val opponent = driver.getOpponent(you)
        val bear = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val zenith = driver.putCardInHand(you, "Red Sun's Zenith")
        driver.giveMana(you, Color.RED, 6)
        driver.submit(
            CastSpell(
                you, zenith,
                targets = listOf(ChosenTarget.Permanent(bear)),
                xValue = 2,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        driver.settle()

        withClue("X = 2 is lethal to a 2/2, and the death replacement sends it to exile") {
            driver.getExile(opponent).contains(bear) shouldBe true
            driver.getGraveyardCardNames(opponent).contains("Grizzly Bears") shouldBe false
        }
        withClue("and the spell still shuffles itself in") {
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).contains(zenith) shouldBe true
        }
    }

    test("X = 0 deals no damage, so a creature that dies later still goes to the graveyard") {
        val driver = newDriver()
        val you = driver.player1
        val opponent = driver.getOpponent(you)
        val bear = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val zenith = driver.putCardInHand(you, "Red Sun's Zenith")
        driver.giveMana(you, Color.RED, 4)
        driver.submit(
            CastSpell(
                you, zenith,
                targets = listOf(ChosenTarget.Permanent(bear)),
                xValue = 0,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        driver.settle()

        withClue("0 damage is no damage at all (CR 120.8), so the 2/2 is untouched") {
            driver.state.getZone(ZoneKey(opponent, Zone.BATTLEFIELD)).contains(bear) shouldBe true
        }

        // Kill it with something else, still this turn — the window the marker would have covered.
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.submit(
            CastSpell(
                you, bolt,
                targets = listOf(ChosenTarget.Permanent(bear)),
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        driver.settle()

        withClue("no creature was 'dealt damage this way', so the exile clause never applied — " +
            "an ungated marker would have swallowed this creature into exile") {
            driver.getGraveyardCardNames(opponent).contains("Grizzly Bears") shouldBe true
            driver.getExile(opponent).contains(bear) shouldBe false
        }
    }

    test("the spell shuffles itself into its owner's library on resolution") {
        val driver = newDriver()
        val you = driver.player1
        val opponent = driver.getOpponent(you)
        val librarySizeBefore = driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).size

        val zenith = driver.putCardInHand(you, "Red Sun's Zenith")
        driver.giveMana(you, Color.RED, 6)
        driver.submit(
            CastSpell(
                you, zenith,
                targets = listOf(ChosenTarget.Player(opponent)),
                xValue = 3,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        driver.settle()

        withClue("X damage to a player, and the card returns to the library rather than the bin") {
            driver.getLifeTotal(opponent) shouldBe 17
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).contains(zenith) shouldBe true
            driver.state.getZone(ZoneKey(you, Zone.LIBRARY)).size shouldBe librarySizeBefore + 1
            driver.getGraveyardCardNames(you).contains("Red Sun's Zenith") shouldBe false
        }
    }
})
