package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The fronted duration — "Until end of turn, target creature gets +3/+3." — read by the same rules
 * that read the trailing spelling, and never printed.
 *
 * Every case here is stated as a *pair*: the fronted line and its trailing twin must parse to the
 * same model, and the fronted one must print as the trailing one. Asserting the model alone would
 * pass for a second rule that happened to agree today, which is exactly the configuration
 * [Durations] exists to avoid.
 */
class DurationsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    /** The fronted line reads as its trailing twin and normalizes back to it. */
    fun frontedOf(fronted: String, trailing: String) {
        withClue(fronted) {
            fragment(fronted) shouldBe fragment(trailing)
            Grammar.abilityLine.printLine(fragment(fronted)) shouldBe trailing
        }
    }

    "the derivation moves the duration to the front of a one-sentence template" {
        Durations.fronted("target {filter} gets {mod} until end of turn") shouldBe
            "until end of turn, target {filter} gets {mod}"
    }

    // A rule may span two sentences and the duration belongs to the last one — prefixing the whole
    // template would say the choice happens for a turn.
    "the derivation fronts into the last sentence of a two-sentence template" {
        Durations.fronted("choose a creature type. each creature you control becomes that type until end of turn") shouldBe
            "choose a creature type. until end of turn, each creature you control becomes that type"
    }

    "a template with no duration to move is refused rather than silently unchanged" {
        runCatching { Durations.fronted("draw a card") }.isFailure shouldBe true
    }

    "a targeted pump reads fronted and prints trailing" {
        frontedOf(
            "Until end of turn, target creature gets +3/+3.",
            "Target creature gets +3/+3 until end of turn.",
        )
    }

    "a targeted grant, and a pump and grant, read fronted" {
        frontedOf(
            "Until end of turn, target creature gains flying.",
            "Target creature gains flying until end of turn.",
        )
        frontedOf(
            "Until end of turn, target creature gets +2/+2 and gains trample and lifelink.",
            "Target creature gets +2/+2 and gains trample and lifelink until end of turn.",
        )
    }

    // Titanic Ultimatum and Volatile Claws' shape — the group sentences, which is where the corpus
    // fronts the duration most often.
    "a group pump, grant, and the two together read fronted" {
        frontedOf(
            "Until end of turn, creatures you control get +1/+1.",
            "Creatures you control get +1/+1 until end of turn.",
        )
        frontedOf(
            "Until end of turn, creatures you control gain trample.",
            "Creatures you control gain trample until end of turn.",
        )
        frontedOf(
            "Until end of turn, creatures you control get +5/+5 and gain first strike, trample, and lifelink.",
            "Creatures you control get +5/+5 and gain first strike, trample, and lifelink until end of turn.",
        )
    }

    // Resplendent Angel — the fronted spelling inside an activated ability, where the sentence
    // starts after the cost's colon rather than at the line's first character.
    "the fronted duration reads after an activation cost" {
        frontedOf(
            "{3}{W}{W}{W}: Until end of turn, ~ gets +2/+2 and gains lifelink.",
            "{3}{W}{W}{W}: ~ gets +2/+2 and gains lifelink until end of turn.",
        )
    }

    "the source's own durational clauses read fronted" {
        frontedOf(
            "Until end of turn, ~ gains flying.",
            "~ gains flying until end of turn.",
        )
        frontedOf(
            "Until end of turn, ~ loses flying.",
            "~ loses flying until end of turn.",
        )
    }

    // Heroic Reinforcements — the fronted clause as the *second* sentence of a run, which is the
    // position `Steps.sequence` reaches and the one a line-initial rule would miss.
    "the fronted duration reads as a later clause in a sequence" {
        frontedOf(
            "Create two 1/1 white Soldier creature tokens. Until end of turn, creatures you control get +1/+1 and gain haste.",
            "Create two 1/1 white Soldier creature tokens. Creatures you control get +1/+1 and gain haste until end of turn.",
        )
    }

    // The meta-test every family gets: a `match` half that quietly matches nothing would show up as a
    // print mismatch far from its cause. Here it doubles as the proof that the fronted spellings
    // never print — every one of these lines is *entered* fronted and *leaves* trailing.
    "every durational rule still prints its trailing spelling" {
        listOf(
            "Target creature gets +3/+3 until end of turn.",
            "Target creature gains flying until end of turn.",
            "Target creature gets +2/+2 and gains trample until end of turn.",
            "Creatures you control get +1/+1 until end of turn.",
            "Creatures you control gain trample until end of turn.",
            "Creatures you control get +2/+2 and gain flying until end of turn.",
            "~ gets +1/+1 until end of turn.",
            "~ gains trample until end of turn.",
            "~ loses flying until end of turn.",
            "~ gets +2/+2 and gains haste until end of turn.",
        ).forEach { line -> Grammar.abilityLine.printLine(fragment(line)) shouldBe line }
    }
})
