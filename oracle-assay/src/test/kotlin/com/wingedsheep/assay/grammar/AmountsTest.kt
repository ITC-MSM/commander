package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CharacteristicValue
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.Aggregation
import com.wingedsheep.sdk.scripting.values.CardNumericProperty
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The counting band — [Amounts.count] as one vocabulary, and the three positions that slot it: the
 * characteristic-defining stat box, the counted verbs' "equal to …" clause, and the where-clause
 * sentences that already existed.
 *
 * The assertions worth having here are the ones about **which** value a phrase denotes and which it
 * refuses. A tally of your graveyard and a tally of your battlefield round-trip equally well under
 * each other's reading — that is precisely the reversible-but-wrong class, and it is the bug the
 * differential found in a hand-written Revenant on the day this band could read its line.
 */
class AmountsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    fun declines(line: String) {
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    fun dynamic(line: String): DynamicAmount =
        (fragment(line).dynamicPower as CharacteristicValue.Dynamic).source

    // ---------------------------------------------------------------------------------------
    // The vocabulary
    // ---------------------------------------------------------------------------------------

    "a battlefield tally names whose battlefield it scans" {
        dynamic("~'s power and toughness are each equal to the number of Swamps you control.") shouldBe
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land.withSubtype("Swamp"))
        dynamic("~'s power and toughness are each equal to the number of creatures on the battlefield.") shouldBe
            DynamicAmount.AggregateBattlefield(Player.Each, GameObjectFilter.Creature)
        roundTrips("~'s power and toughness are each equal to the number of Swamps you control.")
        roundTrips("~'s power and toughness are each equal to the number of creatures on the battlefield.")
    }

    // Revenant's bug, as a property. The two readings are byte-identical apart from the zone.
    "a zone tally counts cards, and is not the battlefield tally" {
        dynamic("~'s power and toughness are each equal to the number of creature cards in your graveyard.") shouldBe
            DynamicAmount.Count(Player.You, Zone.GRAVEYARD, GameObjectFilter.Creature)
        dynamic("~'s power and toughness are each equal to the number of creature cards in all graveyards.") shouldBe
            DynamicAmount.Count(Player.Each, Zone.GRAVEYARD, GameObjectFilter.Creature)
        roundTrips("~'s power and toughness are each equal to the number of creature cards in your graveyard.")
        roundTrips("~'s power and toughness are each equal to the number of creature cards in all graveyards.")
    }

    "an unfiltered zone tally is its own sentence, with no noun to slot" {
        dynamic("~'s power and toughness are each equal to the number of cards in your hand.") shouldBe
            DynamicAmount.Count(Player.You, Zone.HAND)
        roundTrips("~'s power and toughness are each equal to the number of cards in your hand.")
        roundTrips("~'s power and toughness are each equal to the number of cards in your graveyard.")
    }

    "the aggregation layer owns one field and leaves the noun phrase alone" {
        dynamic("~'s power is equal to the greatest mana value among artifacts you control.") shouldBe
            DynamicAmount.AggregateBattlefield(
                Player.You,
                GameObjectFilter.Artifact,
                Aggregation.MAX,
                CardNumericProperty.MANA_VALUE,
            )
        dynamic("~'s power is equal to the number of colors among permanents you control.") shouldBe
            DynamicAmount.AggregateBattlefield(
                Player.You,
                GameObjectFilter.Permanent,
                Aggregation.DISTINCT_COLORS,
            )
        roundTrips("~'s power is equal to the greatest mana value among artifacts you control.")
        roundTrips("~'s power is equal to the number of colors among permanents you control.")
        roundTrips("~'s power is equal to the greatest power among creatures you control.")
        roundTrips("~'s power is equal to the number of basic land types among lands you control.")
    }

    "the multiplier is a layer, and only the word English has" {
        dynamic("~'s power and toughness are each equal to twice the number of cards in your hand.") shouldBe
            DynamicAmount.Multiply(DynamicAmount.Count(Player.You, Zone.HAND), 2)
        roundTrips("~'s power and toughness are each equal to twice the number of cards in your hand.")
        declines("~'s power and toughness are each equal to three times the number of cards in your hand.")
    }

    "your life total is a count with no noun in it at all" {
        dynamic("~'s power and toughness are each equal to your life total.") shouldBe
            DynamicAmount.YourLifeTotal
        roundTrips("~'s power and toughness are each equal to your life total.")
    }

    // ---------------------------------------------------------------------------------------
    // The characteristic-defining line — three shapes and the pairing rule
    // ---------------------------------------------------------------------------------------

    "the joined form defines both characteristics from one clause" {
        val fragment = fragment("~'s power and toughness are each equal to the number of lands you control.")
        val count = DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land)
        fragment.dynamicPower shouldBe CharacteristicValue.Dynamic(count)
        fragment.dynamicToughness shouldBe CharacteristicValue.Dynamic(count)
        fragment.script shouldBe com.wingedsheep.sdk.model.CardScript.EMPTY
    }

    "each single-characteristic form defines only its own half" {
        fragment("~'s power is equal to the number of lands you control.").dynamicToughness shouldBe null
        fragment("~'s toughness is equal to the number of lands you control.").dynamicPower shouldBe null
        roundTrips("~'s power is equal to the number of lands you control.")
        roundTrips("~'s toughness is equal to the number of lands you control.")
    }

    // Yavimaya Kavu: two lines, two characteristics, one card.
    "two single-characteristic lines fold, and two of the same one do not" {
        val power = fragment("~'s power is equal to the number of lands you control.")
        val toughness = fragment("~'s toughness is equal to the number of creatures you control.")
        val folded = power.merge(toughness)!!
        folded.dynamicPower shouldBe
            CharacteristicValue.Dynamic(DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land))
        folded.dynamicToughness shouldBe
            CharacteristicValue.Dynamic(DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature))
        power.merge(power) shouldBe null
    }

    // Lhurgoyf's shape: one amount, named once and used twice, with the offset on the second half.
    "the plus-one sibling stores the amount once and adds the offset" {
        val line = "~'s power is equal to the number of creature cards in all graveyards " +
            "and its toughness is equal to that number plus 1."
        val count = DynamicAmount.Count(Player.Each, Zone.GRAVEYARD, GameObjectFilter.Creature)
        fragment(line).dynamicPower shouldBe CharacteristicValue.Dynamic(count)
        fragment(line).dynamicToughness shouldBe CharacteristicValue.DynamicWithOffset(count, 1)
        roundTrips(line)
    }

    // ---------------------------------------------------------------------------------------
    // The counted verbs' second spelling
    // ---------------------------------------------------------------------------------------

    // Life is not in the band: "for each" is its canonical spelling and outnumbers "equal to"
    // 131 to 23, so offering both would be two printed forms for one model. See
    // [Steps.countedSteps] for what it would take to unify them.
    "life keeps its numeral and declines the clause the damage verbs read" {
        roundTrips("You gain 3 life.")
        declines("You gain life equal to the number of creatures you control.")
    }

    "damage puts the clause where the amount's shape says it goes" {
        // A tally is a heavy noun phrase and trails the recipient…
        roundTrips("~ deals damage to target creature equal to the number of Mountains you control.")
        roundTrips("~ deals damage to any target equal to the number of cards in your hand.")
        // …and a property of an object is light and leads.
        roundTrips("~ deals damage equal to the number of +1/+1 counters on ~ to any target.")
        roundTrips("~ deals damage equal to the number of +1/+1 counters on ~ to target creature.")
    }

    // The minority order for each domain parses and comes back as the majority one: the reading
    // survived, only the spelling moved, which is what an `alternate` is for.
    "the minority word order reads and reprints as the canonical one" {
        Grammar.abilityLine.printLine(
            fragment("~ deals damage equal to the number of Mountains you control to target creature.")
        ) shouldBe "~ deals damage to target creature equal to the number of Mountains you control."
        Grammar.abilityLine.printLine(
            fragment("~ deals damage to any target equal to the number of +1/+1 counters on ~.")
        ) shouldBe "~ deals damage equal to the number of +1/+1 counters on ~ to any target."
    }
})
