package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.mechanics.combat.CombatTaxes
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.MyrPrototype
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Myr Prototype (MRD #214) — "At the beginning of your upkeep, put a +1/+1 counter on this
 * creature. This creature can't attack or block unless you pay {1} for each +1/+1 counter on it."
 *
 * The card is a self-taxer, which is a different animal from Ghostly Prison: the permanent charging
 * the tax and the creature paying it are the same object, so the amount has to be read off the
 * *declared* creature rather than off the board. Two mistakes are available and both look fine on a
 * one-creature board — reading the counters of some other permanent, and charging every declared
 * creature the Prototype's price. The counter-holding bystander and the untaxed co-attacker below
 * are there to fail on exactly those.
 *
 * The zero-counter case matters too: a Prototype that just resolved owes nothing, so the tax must
 * be a plain 0 rather than a floor of {1} that quietly stops a fresh Myr from attacking.
 *
 * Amounts are asserted through [CombatTaxes] directly, since that is the shared pricing entry point
 * the AI reads as well; one declaration test then proves the price is actually charged.
 */
class MyrPrototypeScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + MyrPrototype)
        d.initMirrorMatch(deck = Deck.of("Forest" to 30), skipMulligans = true)
        return d
    }

    fun GameTestDriver.withCounters(entityId: EntityId, count: Int) =
        addComponent(entityId, CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to count)))

    fun GameTestDriver.counters(entityId: EntityId): Int =
        state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    /** What the shared pricing entry point charges to send [attackers] at [defender]. */
    fun GameTestDriver.attackPrice(attackers: List<EntityId>, defender: EntityId): Int =
        CombatTaxes.attackTax(
            state,
            cardRegistry,
            attackers.associateWith { defender },
            state.projectedState,
        )

    test("a Prototype with no counters attacks for free") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)
        val myr = d.putCreatureOnBattlefield(attacker, "Myr Prototype")
        d.removeSummoningSickness(myr)

        withClue("{1} per counter, and there are none — not a floor of one") {
            d.attackPrice(listOf(myr), defender) shouldBe 0
        }
    }

    test("the tax is {1} for each +1/+1 counter on the Prototype itself") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)
        val myr = d.putCreatureOnBattlefield(attacker, "Myr Prototype")
        d.removeSummoningSickness(myr)
        d.withCounters(myr, 3)

        withClue("three counters, three generic") {
            d.attackPrice(listOf(myr), defender) shouldBe 3
        }
    }

    test("counters on another creature don't raise the price") {
        // The amount is EntityReference.Source, not a board aggregate: a fat bystander is irrelevant.
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)
        val myr = d.putCreatureOnBattlefield(attacker, "Myr Prototype")
        val bear = d.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        d.removeSummoningSickness(myr)
        d.withCounters(myr, 1)
        d.withCounters(bear, 5)

        withClue("only the Prototype's own counter is charged") {
            d.attackPrice(listOf(myr), defender) shouldBe 1
        }
    }

    test("a co-attacker without the ability is not charged the Prototype's price") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)
        val myr = d.putCreatureOnBattlefield(attacker, "Myr Prototype")
        val bear = d.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        d.removeSummoningSickness(myr)
        d.removeSummoningSickness(bear)
        d.withCounters(myr, 2)

        withClue("the tax is self-scoped, so two attackers still owe only the Prototype's {2}") {
            d.attackPrice(listOf(myr, bear), defender) shouldBe 2
        }
        withClue("dropping the Prototype drops the whole charge — the tax stays monotone") {
            d.attackPrice(listOf(bear), defender) shouldBe 0
        }
    }

    test("blocking is taxed on the same terms as attacking") {
        val d = driver()
        val attacker = d.activePlayer!!
        val blocker = d.getOpponent(attacker)
        val myr = d.putCreatureOnBattlefield(blocker, "Myr Prototype")
        d.withCounters(myr, 2)

        withClue("'can't attack or block' is one sentence and one price") {
            CombatTaxes.blockTax(d.state, d.cardRegistry, setOf(myr), d.state.projectedState) shouldBe 2
        }
    }

    test("declaring a counter-laden Prototype as an attacker pauses to collect the tax") {
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)
        val myr = d.putCreatureOnBattlefield(attacker, "Myr Prototype")
        d.removeSummoningSickness(myr)
        // Two untapped lands so the attacker can actually pay what they now owe.
        d.putLandOnBattlefield(attacker, "Forest")
        d.putLandOnBattlefield(attacker, "Forest")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        // Set the counters after the upkeep trigger has had its turn, so the price is exactly two
        // whether or not this Myr caught its own upkeep.
        d.withCounters(myr, 2)
        val result = d.declareAttackers(attacker, listOf(myr), defender)

        withClue("the price is owed, so the declaration pauses for mana rather than resolving free") {
            result.newState.pendingDecision.shouldBeInstanceOf<SelectManaSourcesDecision>()
        }
    }

    test("the upkeep trigger is what makes the price climb") {
        // The two printed lines are one joke: each upkeep adds the counter that raises the tax.
        val d = driver()
        val attacker = d.activePlayer!!
        val defender = d.getOpponent(attacker)
        val myr = d.putCreatureOnBattlefield(attacker, "Myr Prototype")
        d.removeSummoningSickness(myr)

        withClue("before any upkeep has resolved the Myr is free to swing") {
            d.attackPrice(listOf(myr), defender) shouldBe 0
        }

        d.passPriorityUntil(Step.UPKEEP)
        d.bothPass()

        withClue("one upkeep, one counter, {1} to swing") {
            d.counters(myr) shouldBe 1
            d.attackPrice(listOf(myr), defender) shouldBe 1
        }
    }
})
