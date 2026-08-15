package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.eoe.cards.DyadrineSynthesisAmalgam
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dyadrine, Synthesis Amalgam ({X}{G}{W}, Legendary Artifact Creature — Construct, 0/1):
 *  "Dyadrine enters with a number of +1/+1 counters on it equal to the amount of mana
 *   spent to cast it."
 *
 * Regression for the `EntersWithDynamicCounters` + [DynamicAmount.TotalManaSpent] plumbing:
 * the ETB replacement effect was evaluated with an empty context, so `TotalManaSpent`
 * resolved to its default 0 and Dyadrine entered with no counters. The fix threads the
 * spell's mana-spent buckets into the replacement-effect context at resolution time.
 */
class DyadrineSynthesisAmalgamTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DyadrineSynthesisAmalgam)
        return driver
    }

    test("enters with +1/+1 counters equal to total mana spent (X=3 → {3}{G}{W} = 5)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val spell = driver.putCardInHand(activePlayer, "Dyadrine, Synthesis Amalgam")
        // {3}{G}{W} with X=3 → 5 total mana.
        driver.giveMana(activePlayer, Color.GREEN, 1)
        driver.giveMana(activePlayer, Color.WHITE, 1)
        driver.giveColorlessMana(activePlayer, 3)

        val cast = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = spell,
                xValue = 3,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        cast.isSuccess shouldBe true
        driver.bothPass()

        // Dyadrine resolved onto the battlefield with 5 +1/+1 counters.
        driver.state.getBattlefield().contains(spell) shouldBe true

        val counters = driver.state.getEntity(spell)?.get<CountersComponent>()
        val plusCounters = counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        plusCounters shouldBe 5
    }

    test("X=0: enters with counters equal to the {G}{W} paid (= 2)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val activePlayer = driver.activePlayer!!
        val spell = driver.putCardInHand(activePlayer, "Dyadrine, Synthesis Amalgam")
        driver.giveMana(activePlayer, Color.GREEN, 1)
        driver.giveMana(activePlayer, Color.WHITE, 1)

        val cast = driver.submit(
            CastSpell(
                playerId = activePlayer,
                cardId = spell,
                xValue = 0,
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        cast.isSuccess shouldBe true
        driver.bothPass()

        val counters = driver.state.getEntity(spell)?.get<CountersComponent>()
        val plusCounters = counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        plusCounters shouldBe 2
    }
})
