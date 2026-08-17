package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.ConnectingTheDots
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Connecting the Dots — "Whenever a creature you control attacks, exile the top card of your
 * library face down. … {1}{R}, Discard your hand, Sacrifice this enchantment: Put all cards exiled
 * with this enchantment into their owners' hands."
 *
 * The two halves only work if they agree on *which* pile: the trigger stamps `linkToSource` and the
 * activated ability reads that same linked pile back. A round trip is the only assertion that
 * catches a mismatch — an exile that isn't linked looks perfectly correct until the payoff returns
 * nothing.
 *
 * The trigger is per-attacker, so the second test attacks with two creatures and expects two cards
 * exiled. Wiring it to the once-per-declaration `YouAttack` event instead would pass the first test
 * and fail only here.
 */
class ConnectingTheDotsScenarioTest : FunSpec({

    val cashInAbility = ConnectingTheDots.activatedAbilities.first().id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ConnectingTheDots)
        return driver
    }

    test("attacking exiles the top card, and the payoff returns exactly that card to hand") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val dots = driver.putPermanentOnBattlefield(active, "Connecting the Dots")
        val attacker = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        driver.removeSummoningSickness(attacker)
        driver.putCardOnTopOfLibrary(active, "Serra Angel")

        val exileBefore = driver.getExile(active).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(active, listOf(attacker), opponent).error shouldBe null
        driver.bothPass() // resolve the attack trigger

        withClue("one attacker, one card off the top into exile") {
            driver.getExile(active).size shouldBe exileBefore + 1
            driver.getExileCardNames(active) shouldContain "Serra Angel"
        }

        // Cash in. The cost discards the opening hand, so whatever is left is what the ability put there.
        driver.giveMana(active, Color.RED, 2)
        driver.submitSuccess(ActivateAbility(active, dots, cashInAbility))
        driver.bothPass() // resolve the ability

        withClue("the linked pile — and only it — comes back to hand") {
            driver.getHand(active).map { driver.getCardName(it) } shouldBe listOf("Serra Angel")
        }
        withClue("the enchantment was sacrificed as part of the cost") {
            driver.getPermanents(active) shouldNotContain dots
        }
    }

    test("the trigger is per attacker — two attackers exile two cards") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(active, "Connecting the Dots")
        val first = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val second = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        driver.removeSummoningSickness(first)
        driver.removeSummoningSickness(second)

        val exileBefore = driver.getExile(active).size

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(active, listOf(first, second), opponent).error shouldBe null
        driver.bothPass() // resolve the first trigger
        driver.bothPass() // resolve the second

        withClue("'whenever a creature you control attacks' fires once per attacker (CR 603.2c)") {
            driver.getExile(active).size shouldBe exileBefore + 2
        }
    }
})
