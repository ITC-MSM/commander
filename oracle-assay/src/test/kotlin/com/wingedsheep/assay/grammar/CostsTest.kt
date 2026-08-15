package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The cost clause: an atom vocabulary and the comma-joined run of it.
 *
 * Two properties are load-bearing and neither is obvious. A composite's order is the *printed* one,
 * because `AbilityCost.Composite` is a list and every hand-written card writes mana before tap. And
 * a cost is the one clause Oracle capitalizes that is not a sentence start, so it has to read in
 * both cases and print in exactly one — see [Costs] for why.
 */
class CostsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    "a single atom is the cost itself and not a one-element composite" {
        roundTrips("{T}: Draw a card.")
        roundTrips("{2}: Draw a card.")
    }

    "atoms join into a composite in the order the card prints them" {
        roundTrips("{2}{B}, {T}, Sacrifice a Goblin creature: Destroy target land.")
        roundTrips("{1}, {T}: Draw a card.")
    }

    // The whole point of the both-cases pairing: the same atom is capitalized mid-line and
    // lowercased by the sentence-case pass at a line start, and both have to come back byte-exact.
    "a verb atom reads capitalized mid-line and lowercased at a line start" {
        roundTrips("{T}, Sacrifice a Goblin creature: Draw a card.")
        roundTrips("Sacrifice a Goblin creature: Draw a card.")
    }

    // The self-reference is spelled `~` here for the reason every rule in the grammar spells it that
    // way: `Normalizer` abstracts the card's own noun before the grammar sees a line, and restores
    // it afterwards. These tests feed the grammar directly, so they feed it the abstracted token.
    "the source paying with itself is its own atom rather than a filtered sacrifice" {
        roundTrips("{T}, Sacrifice ~: Draw a card.")
        roundTrips("{3}{W}, Exile ~: Draw a card.")
    }

    "a counted sacrifice takes a plural noun and refuses the singular's count" {
        roundTrips("{T}, Sacrifice three Cleric creatures: Draw a card.")
    }

    "the tap-permanents cost spells its own rules as literals, not as filter fields" {
        roundTrips("Tap two untapped Bird creatures you control: Draw a card.")
    }

    "paying life is a numeral, per Oracle's convention for quantities of life" {
        roundTrips("{B}, Pay 1 life: Draw a card.")
    }
})
