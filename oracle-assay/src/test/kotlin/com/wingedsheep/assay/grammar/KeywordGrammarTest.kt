package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class KeywordGrammarTest : StringSpec({

    fun parse(line: String): List<KeywordAbility> =
        Grammar.abilityLine.parseLine(line)
            .shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value.keywordAbilities

    fun script(line: String): CardScript =
        Grammar.abilityLine.parseLine(line)
            .shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value.script

    fun roundTrips(line: String) {
        val fragment = Grammar.abilityLine.parseLine(line)
            .shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value
        Grammar.abilityLine.printLine(fragment) shouldBe line
    }

    "a simple keyword line round-trips" {
        parse("Flying") shouldContainExactly listOf(KeywordAbility.of(Keyword.FLYING))
        roundTrips("Flying")
    }

    "sentence case follows position, not the rule" {
        parse("Flying, vigilance") shouldContainExactly
            listOf(KeywordAbility.of(Keyword.FLYING), KeywordAbility.of(Keyword.VIGILANCE))
        roundTrips("Flying, vigilance")
        roundTrips("Vigilance, flying")
    }

    "a vanilla card's absent line is a rule, not a special case" {
        parse("") shouldContainExactly emptyList()
        roundTrips("")
    }

    "parameterized keywords carry their parameter both ways" {
        parse("Ward {2}") shouldContainExactly listOf(KeywordAbility.ward("{2}"))
        roundTrips("Ward {2}")
        roundTrips("Ward—Pay 3 life.")
        roundTrips("Annihilator 2")
        roundTrips("Crew 3")
        roundTrips("Flashback {3}{R}")
        roundTrips("Cycling {2}")
        roundTrips("Suspend 4—{1}{R}")
        roundTrips("Impending 4—{2}{W}{W}")
        roundTrips("Splice onto Arcane {1}{U}")
        roundTrips("Kicker {2}{G}")
        roundTrips("Multikicker {1}")
        roundTrips("Morph {2}{U}")
        roundTrips("Basic landcycling {1}{U}")
        roundTrips("Forestcycling {2}")
    }

    "protection reads every quality it can express" {
        parse("Protection from black") shouldContainExactly
            listOf(KeywordAbility.Protection(ProtectionScope.Color(Color.BLACK)))
        roundTrips("Protection from black")
        roundTrips("Protection from everything")
        roundTrips("Protection from each opponent")
        roundTrips("Protection from artifacts")
        roundTrips("Protection from Goblins")
    }

    "a multi-colour protection is one rule, so there is exactly one reading" {
        parse("Protection from white and from blue") shouldContainExactly listOf(
            KeywordAbility.Protection(ProtectionScope.Colors(setOf(Color.WHITE, Color.BLUE)))
        )
        roundTrips("Protection from white and from blue")
    }

    "an irregular plural is not de-pluralized into a subtype that does not exist" {
        parse("Protection from Elves") shouldContainExactly
            listOf(KeywordAbility.Protection(ProtectionScope.Subtype("Elf")))
        roundTrips("Protection from Elves")
    }

    // The differential gate found this on its first run: "Plains" naively de-pluralizes to `Plain`,
    // which round-trips perfectly ("Plain" + "s") while naming a type the SDK does not have —
    // `Subtype.PLAINS` is `Plains`. Only checking against the SDK's own type list catches it.
    "an invariant plural keeps its own spelling rather than losing its s" {
        parse("Affinity for Plains") shouldContainExactly
            listOf(KeywordAbility.AffinityForSubtype(Subtype("Plains")))
        roundTrips("Affinity for Plains")
    }

    "the ordinary plural still wins where both readings would name a real type" {
        parse("Protection from Zombies") shouldContainExactly
            listOf(KeywordAbility.Protection(ProtectionScope.Subtype("Zombie")))
        roundTrips("Protection from Zombies")
    }

    "a consonant-y type pluralizes as -ies, and a vowel-y one does not" {
        roundTrips("Protection from Allies")
        roundTrips("Protection from Monkeys")
    }

    "a -ves plural resolves to its -f singular in both directions" {
        parse("Protection from Werewolves") shouldContainExactly
            listOf(KeywordAbility.Protection(ProtectionScope.Subtype("Werewolf")))
        roundTrips("Protection from Werewolves")
    }

    // The SDK publishes a list for creature and basic land types only, so artifact / enchantment /
    // nonbasic-land types are real subtypes it cannot confirm. Ranking rather than gating on the
    // list is what keeps them working: no candidate is known, so the ordinary reading stands.
    "a subtype the SDK's lists do not cover still reads by the ordinary rule" {
        parse("Affinity for Gates") shouldContainExactly
            listOf(KeywordAbility.AffinityForSubtype(Subtype("Gate")))
        roundTrips("Affinity for Gates")
        roundTrips("Affinity for Foods")
    }

    "a long real keyword line round-trips whole" {
        roundTrips("Flying, first strike, vigilance, trample, haste, protection from black and from red")
    }

    "the semicolon separator parses and normalizes to the canonical comma" {
        parse("Flying; banding") shouldContainExactly
            listOf(KeywordAbility.of(Keyword.FLYING), KeywordAbility.of(Keyword.BANDING))
        val fragment = Grammar.abilityLine.parseLine("Flying; banding")
            .shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value
        Grammar.abilityLine.printLine(fragment) shouldBe "Flying, banding"
    }

    "text outside the grammar declines, and says where" {
        val declined = Grammar.abilityLine.parseLine("Enchant creature")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
        declined.reason shouldBe com.wingedsheep.assay.syntax.DeclineReason.NO_PARSE
    }

    "a mana symbol the SDK cannot express declines rather than throwing" {
        Grammar.abilityLine.parseLine("Cycling {S}").shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    "every keyword rule can print what it parses" {
        // Guards the one failure mode a per-rule test cannot: a `match` half that quietly matches
        // nothing, which would show up on the corpus as a print mismatch far from its cause.
        val samples = listOf(
            "Flying", "Trample", "First strike", "Double strike", "Protection from red",
            "Hexproof from black", "Ward {1}", "Toxic 1", "Devour 2", "Casualty 1",
            "Affinity for artifacts", "Affinity for Lizards", "Conspire", "Flanking", "Increment",
            "Foretell {1}{U}", "Plot {2}{G}", "Disturb {1}{W}", "Evoke {3}{B}", "Emerge {6}{U}",
            "Miracle {W}", "Dash {1}{R}", "Warp {2}{U}", "Cleave {3}{U}{U}", "Harmonize {4}{G}",
            "Mayhem {1}{B}", "Ninjutsu {1}{U}", "Sneak {B}", "Web-slinging {2}{W}",
            "Disguise {1}{U}", "Offspring {2}", "Madness {1}{R}", "Hideaway 4", "Saddle 2",
            "Start your engines!", "Ascend", "Riot", "Bushido 1", "Modular 3", "Fading 5",
            "Vanishing 3", "Renown 1", "Fabricate 2", "Tribute 3", "Mobilize 1", "Firebending 2",
            "Rampage 2", "Absorb 1", "Afflict 3",
        )
        samples.forEach { roundTrips(it) }
    }
})
