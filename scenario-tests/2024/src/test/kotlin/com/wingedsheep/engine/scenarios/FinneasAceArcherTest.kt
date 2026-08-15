package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.FinneasAceArcher
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Finneas, Ace Archer (BLB): "Whenever Finneas attacks, put a +1/+1 counter on each
 * other CREATURE you control that's a token or a Rabbit."
 *
 * Regression guard: the token branch of the filter used to match any token — a Food
 * (noncreature artifact token) wrongly received +1/+1 counters. Only creature tokens
 * and Rabbits qualify.
 */
class FinneasAceArcherTest : FunSpec({

    val testProvisions: CardDefinition = card("Test Provisions") {
        manaCost = "{G}"
        typeLine = "Sorcery"
        spell {
            effect = Effects.CreateFood(1)
                .then(
                    Effects.CreateToken(
                        power = 2,
                        toughness = 2,
                        colors = setOf(Color.GREEN),
                        creatureTypes = setOf("Bear")
                    )
                )
        }
    }

    val testRabbit: CardDefinition = CardDefinition.creature(
        name = "Test Rabbit",
        manaCost = ManaCost.parse("{W}"),
        subtypes = setOf(Subtype("Rabbit")),
        power = 1,
        toughness = 1
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + com.wingedsheep.mtg.sets.tokens.PredefinedTokens.allTokens +
                listOf(FinneasAceArcher, testProvisions, testRabbit)
        )
        return driver
    }

    fun GameTestDriver.plusCounters(id: com.wingedsheep.sdk.model.EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("attack trigger puts counters on creature tokens and Rabbits, NOT on Food tokens") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        var active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        active = driver.activePlayer!!

        val finneas = driver.putCreatureOnBattlefield(active, "Finneas, Ace Archer")
        driver.removeSummoningSickness(finneas)
        val rabbit = driver.putCreatureOnBattlefield(active, "Test Rabbit")
        val nonRabbitNontoken = driver.putCreatureOnBattlefield(active, "Savannah Lions")

        // Create a Food token and a Bear creature token via a real spell so both are tokens.
        driver.giveMana(active, Color.GREEN, 1)
        val provisions = driver.putCardInHand(active, "Test Provisions")
        driver.castSpell(active, provisions)
        driver.bothPass()

        val food = driver.findPermanent(active, "Food")
        food.shouldNotBeNull()
        val bearToken = driver.findPermanent(active, "Bear Token")
        bearToken.shouldNotBeNull()

        // Attack with Finneas → the counter trigger resolves.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        var safety = 0
        while (driver.activePlayer != active && safety < 50) {
            driver.bothPass()
            driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
            safety++
        }
        driver.declareAttackers(active, listOf(finneas), driver.getOpponent(active))
        driver.bothPass()

        // Creature token and Rabbit get a counter; Food token, non-Rabbit nontoken
        // creature, and Finneas itself ("each OTHER creature") do not.
        driver.plusCounters(bearToken) shouldBe 1
        driver.plusCounters(rabbit) shouldBe 1
        driver.plusCounters(food) shouldBe 0
        driver.plusCounters(nonRabbitNontoken) shouldBe 0
        driver.plusCounters(finneas) shouldBe 0
    }
})
