package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The top-of-library band — one gather/select/move vocabulary in three layers, and the sentences
 * that lift it.
 *
 * The assertions that earn their place here are the ones about a *field the printer could get wrong*
 * rather than about a sentence parsing. Three of them are the band's whole argument:
 *
 * - the remainder's **order** is carried, so "in a random order" and "in any order" are different
 *   values rather than two spellings of one — the field five hand-written cards were dropping;
 * - "the other" and "the rest" take **disjoint** halves of one value space, so neither the
 *   alternation's order nor its membership can decide which prints;
 * - the impulse anaphor **agrees with the count**, in both directions, and refuses when it does not.
 */
class TopOfLibraryTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun effect(line: String) = fragment(line).script.spellEffect

    fun steps(line: String): List<*> = (effect(line) as CompositeEffect).effects

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    /** An alternate spelling: it parses to the same model and prints as [canonical]. */
    fun variantOf(line: String, canonical: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe canonical
        effect(line) shouldBe effect(canonical)
    }

    fun declines(line: String) {
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    // ---------------------------------------------------------------------------------------
    // Layer 1 — the count, and the noun that agrees with it
    // ---------------------------------------------------------------------------------------

    "the count layer carries the noun, so one and many are one slot" {
        (steps("Exile the top card of your library.")[0] as GatherCardsEffect).source shouldBe
            CardSource.TopOfLibrary(DynamicAmount.Fixed(1))
        (steps("Exile the top three cards of your library.")[0] as GatherCardsEffect).source shouldBe
            CardSource.TopOfLibrary(DynamicAmount.Fixed(3))
        (steps("Exile the top X cards of your library.")[0] as GatherCardsEffect).source shouldBe
            CardSource.TopOfLibrary(DynamicAmount.XValue)
        roundTrips("Exile the top card of your library.")
        roundTrips("Exile the top three cards of your library.")
        roundTrips("Exile the top X cards of your library.")
    }

    // The number word and the noun cannot disagree, because they are the same phrase.
    "a count of one is never spelled as a word" {
        declines("Exile the top one cards of your library.")
        declines("Exile the top one card of your library.")
    }

    // ---------------------------------------------------------------------------------------
    // Layer 2/3 — the destination, and the order layer over it
    // ---------------------------------------------------------------------------------------

    "the remainder's order is a value the sentence carries, not a flourish" {
        fun restMove(line: String) =
            steps(line).filterIsInstance<MoveCollectionEffect>().single { it.from == "rest" }

        val random = "Look at the top four cards of your library. Put one of them into your hand and " +
            "the rest on the bottom of your library in a random order."
        val any = "Look at the top four cards of your library. Put one of them into your hand and " +
            "the rest on the bottom of your library in any order."
        val bare = "Look at the top four cards of your library. Put one of them into your hand and " +
            "the rest on the bottom of your library."

        restMove(random).order shouldBe CardOrder.Random
        restMove(any).order shouldBe CardOrder.ControllerChooses
        restMove(bare).order shouldBe CardOrder.Preserve
        // …and the three are genuinely different cards, which is the point: five goldens had lost
        // exactly this field.
        effect(random) shouldNotBe effect(any)
        listOf(random, any, bare).forEach(::roundTrips)
    }

    "the destination layer spells whole prepositional phrases" {
        fun keepDestination(place: String) =
            steps("Look at the top four cards of your library. Put one of them $place and the rest into your graveyard.")
                .filterIsInstance<MoveCollectionEffect>().single { it.from == "kept" }.destination

        keepDestination("into your hand") shouldBe CardDestination.ToZone(Zone.HAND)
        keepDestination("onto the battlefield") shouldBe CardDestination.ToZone(Zone.BATTLEFIELD)
        keepDestination("on the bottom of your library") shouldBe
            CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom)
        keepDestination("on top of your library") shouldBe
            CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Top)
    }

    "the library is elided once the sentence has already named it" {
        variantOf(
            "Look at the top four cards of your library. Put one of them into your hand and the rest " +
                "on the bottom in a random order.",
            "Look at the top four cards of your library. Put one of them into your hand and the rest " +
                "on the bottom of your library in a random order.",
        )
    }

    // Witness the Future writes the recipe as one sentence rather than two. The gather and the
    // selection that consumes it cannot be split into two clauses, so the join is a spelling of
    // this rule and not a member of [Steps]' clause run.
    "the two halves can be joined with a comma instead of a full stop" {
        variantOf(
            "You look at the top four cards of your library, then put one of those cards into your " +
                "hand and the rest on the bottom of your library in a random order.",
            "Look at the top four cards of your library. Put one of them into your hand and the rest " +
                "on the bottom of your library in a random order.",
        )
    }

    "the pile can be named as them or as those cards" {
        variantOf(
            "Look at the top five cards of your library. Put one of those cards into your hand and " +
                "the rest into your graveyard.",
            "Look at the top five cards of your library. Put one of them into your hand and " +
                "the rest into your graveyard.",
        )
    }

    // ---------------------------------------------------------------------------------------
    // "the rest" against "the other" — disjoint on the model
    // ---------------------------------------------------------------------------------------

    // Tower Geist. English writes "the other" when the remainder is exactly one card, and the
    // corpus does it 16 times to 0 — so this is a fact about the numbers, not a spelling to allow.
    "a remainder of exactly one is the other, and nothing else can print it" {
        roundTrips(
            "Look at the top two cards of your library. Put one of them into your hand and the other " +
                "into your graveyard.",
        )
        declines(
            "Look at the top two cards of your library. Put one of them into your hand and the rest " +
                "into your graveyard.",
        )
        // …and the general rule owns every other remainder, including the one it cannot compute.
        roundTrips(
            "Look at the top four cards of your library. Put one of them into your hand and the rest " +
                "into your graveyard.",
        )
        roundTrips(
            "Look at the top X cards of your library. Put two of them into your hand and the rest " +
                "into your graveyard.",
        )
    }

    // ---------------------------------------------------------------------------------------
    // The filtered dig
    // ---------------------------------------------------------------------------------------

    "the filtered dig keeps up to one matching card, and reveals it" {
        val line = "Look at the top three cards of your library. You may reveal a creature or land " +
            "card from among them and put it into your hand. Put the rest on the bottom of your " +
            "library in any order."
        val select = steps(line).filterIsInstance<SelectFromCollectionEffect>().single()
        select.selection shouldBe SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1))
        select.showAllCards shouldBe true
        steps(line).filterIsInstance<MoveCollectionEffect>().single { it.from == "kept" }
            .revealed shouldBe true
        roundTrips(line)
    }

    // ---------------------------------------------------------------------------------------
    // Impulse — the anaphor agrees with the count
    // ---------------------------------------------------------------------------------------

    "impulse grants permission until the duration the sentence names" {
        fun expiry(line: String) =
            steps(line).filterIsInstance<GrantMayPlayFromExileEffect>().single().expiry

        expiry("Exile the top card of your library. You may play that card this turn.") shouldBe
            MayPlayExpiry.EndOfTurn
        expiry(
            "Exile the top card of your library. Until the end of your next turn, you may play that card.",
        ) shouldBe MayPlayExpiry.UntilEndOfNextTurn
        expiry(
            "Exile the top two cards of your library. Until your next end step, you may play those cards.",
        ) shouldBe MayPlayExpiry.UntilNextEndStep
        expiry(
            "Exile the top card of your library. You may play that card for as long as it remains exiled.",
        ) shouldBe MayPlayExpiry.Permanent
    }

    // The count already decides the number. A sentence that disagrees with itself denotes nothing.
    "the anaphor must agree with the count it refers back to" {
        roundTrips("Exile the top card of your library. You may play that card this turn.")
        roundTrips("Exile the top two cards of your library. You may play those cards this turn.")
        declines("Exile the top card of your library. You may play those cards this turn.")
        declines("Exile the top two cards of your library. You may play that card this turn.")
    }

    "the pronoun spellings parse and the noun ones print" {
        variantOf(
            "Exile the top card of your library. You may play it this turn.",
            "Exile the top card of your library. You may play that card this turn.",
        )
        variantOf(
            "Exile the top two cards of your library. You may play them this turn.",
            "Exile the top two cards of your library. You may play those cards this turn.",
        )
    }

    // Which order is canonical flips with the duration, because the corpus flips: "this turn"
    // trails 115 lines to 40, and the two cross-turn durations front 59:16 and 8:5.
    "each duration prints in the order the corpus prints it, and parses in both" {
        variantOf(
            "Exile the top card of your library. Until end of turn, you may play that card.",
            "Exile the top card of your library. You may play that card this turn.",
        )
        variantOf(
            "Exile the top card of your library. You may play that card until the end of your next turn.",
            "Exile the top card of your library. Until the end of your next turn, you may play that card.",
        )
        variantOf(
            "Exile the top card of your library. You may play that card until your next end step.",
            "Exile the top card of your library. Until your next end step, you may play that card.",
        )
    }

    // ---------------------------------------------------------------------------------------
    // The one gather this family must not claim
    // ---------------------------------------------------------------------------------------

    // A mill is the same printed shape and a different value: `TopOfLibrary.isMill` makes CR
    // 701.13's "mill that many plus four instead" apply at the count site. Reading the flag off
    // rather than ignoring it is what stops a mill printing as "exile the top two cards".
    "a mill is not an exile from the top, even where the pipeline shape agrees" {
        val milled = CompositeEffect(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(2), isMill = true),
                    storeAs = "exiled_top",
                ),
                MoveCollectionEffect(from = "exiled_top", destination = CardDestination.ToZone(Zone.EXILE)),
            ),
        )
        effect("Exile the top two cards of your library.") shouldNotBe milled
    }
})
