package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The counter sentences — "Put a +1/+1 counter on target creature.", "…on ~.", "…on it.", and
 * "~ enters with two +1/+1 counters on it."
 *
 * The interesting assertions here are the ones about what *refuses* to read or print: the three
 * sentences differ only in who the counter lands on, and all three round-trip byte-perfectly under
 * the wrong reading, which is the class only this kind of test and the differential can see.
 */
class CountersTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    fun declines(line: String) {
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    "the singular sentence carries its quantity in the article" {
        fragment("Put a +1/+1 counter on target creature.").script.spellEffect shouldBe
            AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, Targets.bound())
        roundTrips("Put a +1/+1 counter on target creature.")
    }

    "the plural sentence spells its count as a word" {
        fragment("Put two -1/-1 counters on target creature you control.").script.spellEffect shouldBe
            AddCountersEffect(Counters.MINUS_ONE_MINUS_ONE, 2, Targets.bound())
        roundTrips("Put two -1/-1 counters on target creature you control.")
        roundTrips("Put three +1/+1 counters on target Sliver creature.")
    }

    // The commonest effect shape in the whole hand-written corpus: 363 of the 951 AddCounters a
    // golden carries are this one.
    "a counter on the source names the source and not a target" {
        fragment("Put a +1/+1 counter on ~.").script shouldBe
            CardScript(spellEffect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self))
        roundTrips("Put a +1/+1 counter on ~.")
        roundTrips("Put two +1/+1 counters on ~.")
    }

    // The two anaphors. "It" is the source in a first clause and the target once a clause has chosen
    // one, and both readings round-trip byte-perfectly — which is exactly why they are two
    // vocabularies reachable from disjoint positions rather than one rule.
    "\"on it\" is the source in a first clause and the chosen target in a later one" {
        fragment("Put a +1/+1 counter on it.").script.spellEffect shouldBe
            AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)

        // The counter lands on the creature the first clause untapped, never on the source.
        val sequence = fragment("Untap target creature. Put a +1/+1 counter on it.")
        val steps = (sequence.script.spellEffect as CompositeEffect).effects
        steps.last() shouldBe AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, Targets.bound())
        (steps.last() == AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)) shouldBe false
        roundTrips("Untap target creature. Put a +1/+1 counter on it.")
    }

    "the entry replacement reads both quantities" {
        fragment("~ enters with a +1/+1 counter on it.").script.replacementEffects shouldBe
            listOf(EntersWithCounters(CounterTypeFilter.PlusOnePlusOne, 1, selfOnly = true))
        roundTrips("~ enters with a +1/+1 counter on it.")
        roundTrips("~ enters with three -1/-1 counters on it.")
    }

    // Gnarlid Colony and the other kicker creatures. The condition is the clause that makes the card
    // worth playing, and a rule that printed the value without it would be byte-perfect and wrong.
    "an entry replacement carrying a condition refuses to print rather than dropping it" {
        val kicked = CardFragment(
            script = CardScript(
                replacementEffects = listOf(
                    EntersWithCounters(CounterTypeFilter.PlusOnePlusOne, 2, selfOnly = true, condition = WasKicked)
                )
            )
        )
        Grammar.abilityLine.printLine(kicked) shouldBe null
    }

    // Hardened Scales' shape: the same type with selfOnly false says something about *other*
    // permanents, and this sentence names the source.
    "an entry replacement that is not about the source refuses to print" {
        val others = CardFragment(
            script = CardScript(
                replacementEffects = listOf(EntersWithCounters(CounterTypeFilter.PlusOnePlusOne, 1))
            )
        )
        Grammar.abilityLine.printLine(others) shouldBe null
    }

    // The leaf is gated on the SDK's own list for creatureSubtype's reason: an ungated one would
    // read any lowercase word as a counter kind and round-trip a counter Magic does not have.
    "a word the SDK does not name as a counter is not a counter kind" {
        CounterType.fromName("growing") shouldBe null
        declines("Put a growing counter on target creature.")
    }

    "the two-word kinds read despite the leaf taking a single regex match" {
        fragment("Put a first strike counter on target creature.").script.spellEffect shouldBe
            AddCountersEffect(Counters.FIRST_STRIKE, 1, Targets.bound())
        roundTrips("Put a first strike counter on target creature.")
    }

    // No counter kind in all 34,882 Oracle texts is ever spelled both ways, so the article is a
    // total function of the kind and the leaf can be its inverse. "an hourglass" is the silent-h
    // case the letter rule alone would get wrong.
    "the indefinite article follows the kind, silent h included" {
        roundTrips("Put an aim counter on target creature.")
        roundTrips("Put an hourglass counter on target creature.")
        roundTrips("Put a stun counter on target creature.")
        declines("Put an stun counter on target creature.")
        declines("Put a aim counter on target creature.")
    }

    // CounterTypeFilter.Named can hold the same string the dedicated cases do, so the two are one
    // value written twice. The grammar emits one and refuses to print the other, which is what keeps
    // a card written the minority way reporting as a divergence instead of quietly agreeing.
    "the Named spelling of a kind that has a dedicated case never prints" {
        Primitives.counterFilter(Counters.PLUS_ONE_PLUS_ONE) shouldBe CounterTypeFilter.PlusOnePlusOne
        Primitives.counterKindOf(CounterTypeFilter.Named(Counters.PLUS_ONE_PLUS_ONE)) shouldBe null
        Primitives.counterKindOf(CounterTypeFilter.Named(Counters.STUN)) shouldBe Counters.STUN

        val named = CardFragment(
            script = CardScript(
                replacementEffects = listOf(
                    EntersWithCounters(CounterTypeFilter.Named(Counters.PLUS_ONE_PLUS_ONE), 1, selfOnly = true)
                )
            )
        )
        Grammar.abilityLine.printLine(named) shouldBe null
    }
})
