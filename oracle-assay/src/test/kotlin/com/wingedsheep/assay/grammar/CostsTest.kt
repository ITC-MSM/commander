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

    // "card" is not a permanent type and [Filters] has no noun for `Any`, which is what keeps the
    // unqualified row and the filtered one from being two spellings of one model.
    "discarding takes the unqualified noun as a row of its own, not as the filter's empty case" {
        roundTrips("Discard a card: Draw a card.")
        roundTrips("Discard a creature card: Draw a card.")
        roundTrips("Discard two cards: Draw a card.")
        roundTrips("Discard a card at random: Draw a card.")
    }

    "the graveyard is a literal in the exile cost, because it is the only zone a cost names" {
        roundTrips("{1}, Exile a creature card from your graveyard: Draw a card.")
        roundTrips("Exile a card from your graveyard: Draw a card.")
        roundTrips("Exile two creature cards from your graveyard: Draw a card.")
    }

    // "Another" is a determiner, so the noun phrase after it carries no article — the difference
    // from the plain sacrifice, whose article comes from the filter's own spelling.
    "excluding the source is the word \"another\", in both the sacrifice and the tap cost" {
        roundTrips("Sacrifice another creature: Draw a card.")
        roundTrips("Tap another untapped Rogue creature you control: Draw a card.")
    }

    "the singular tap cost is a row of its own, because the plural one refuses a count of one" {
        roundTrips("Tap an untapped Cleric creature you control: Draw a card.")
    }

    "returning a permanent you control spells the controller as a literal, like the tap cost" {
        roundTrips("Return a land you control to its owner's hand: Draw a card.")
    }

    // `self = true` is the whole distinction between removing counters from the source and
    // distributing the removal across permanents you control, and "from ~" is how Oracle prints it.
    "counters come off the source, in all three of the quantities Oracle spells" {
        roundTrips("Remove a spore counter from ~: Draw a card.")
        roundTrips("Remove three spore counters from ~: Draw a card.")
        roundTrips("Remove X charge counters from ~: Draw a card.")
    }

    "milling as a cost counts cards and names no filter, because the top of the library is not chosen" {
        roundTrips("{T}, Mill a card: Draw a card.")
        roundTrips("Mill two cards: Draw a card.")
    }

    // The atom vocabulary is shared with the additional-cost line, so a row written for one context
    // has to work in the other. This is the activation side of that pair; RestrictionsTest is the
    // spell side, and between them they are the argument for the shape.
    "a new atom reaches a composite without the run rule knowing about it" {
        roundTrips("{2}, Discard a card, Sacrifice another creature: Draw a card.")
    }
})
