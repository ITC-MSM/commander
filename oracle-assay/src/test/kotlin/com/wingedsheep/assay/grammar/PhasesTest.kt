package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.dsl.Triggers as SdkTriggers
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * "At the beginning of …" — the step-trigger prefix as `Triggers.phase(step, player, binding)`'s
 * own product.
 *
 * Every assertion here names the SDK call the sentence denotes rather than a shape of its own. That
 * is the property the band is *for*: the grammar had thirteen frozen prefixes calling `dsl.Triggers`'
 * frozen constants, and a test that only round-tripped them would have passed just as happily.
 */
class PhasesTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun ability(line: String): TriggeredAbility =
        fragment(line).script.triggeredAbilities.single()

    fun roundTrips(line: String) {
        withClue(line) { Grammar.abilityLine.printLine(fragment(line)) shouldBe line }
    }

    /** A second spelling reads as the canonical one and normalizes back to it — a VARIANT, not a rule. */
    fun spelledAlso(alternate: String, canonical: String) {
        withClue(alternate) {
            fragment(alternate) shouldBe fragment(canonical)
            Grammar.abilityLine.printLine(fragment(alternate)) shouldBe canonical
        }
    }

    fun declines(line: String) {
        withClue(line) {
            Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Declined>()
        }
    }

    // The whole band in one assertion: the prefix is the factory call, the step is one argument and
    // whose turn it is another, and nothing about the sentence is frozen.
    "the prefix denotes the SDK's own step-trigger factory, argument for argument" {
        ability("At the beginning of your upkeep, draw a card.").trigger shouldBe
            SdkTriggers.phase(Step.UPKEEP, Player.You).event
        ability("At the beginning of each opponent's draw step, draw a card.").trigger shouldBe
            SdkTriggers.phase(Step.DRAW, Player.EachOpponent).event
        ability("At the beginning of each player's first main phase, draw a card.").trigger shouldBe
            SdkTriggers.phase(Step.PRECOMBAT_MAIN, Player.Each).event
        ability("At the beginning of the chosen player's upkeep, draw a card.").trigger shouldBe
            SdkTriggers.phase(Step.UPKEEP, Player.ChosenOpponent).event
    }

    // The named constants are calls to the same factory with every argument frozen, so the rules
    // that used to spell them out have to keep producing them exactly.
    "the constants the rules used to name are the same values" {
        ability("At the beginning of your upkeep, draw a card.").trigger shouldBe
            SdkTriggers.YourUpkeep.event
        ability("At the beginning of each end step, draw a card.").trigger shouldBe
            SdkTriggers.EachEndStep.event
        ability("At the beginning of combat on your turn, draw a card.").trigger shouldBe
            SdkTriggers.BeginCombat.event
        ability("At the beginning of each opponent's upkeep, draw a card.").trigger shouldBe
            SdkTriggers.EachOpponentUpkeep.event
        ability("At the beginning of the chosen player's upkeep, draw a card.").trigger shouldBe
            SdkTriggers.ChosenOpponentUpkeep.event
    }

    // The binding is the factory's third argument, and it is what re-scopes "you" to the attached
    // permanent's controller — Unstable Mutation's and Lingering Death's goldens both write it.
    "an attached step trigger is the binding argument and nothing else" {
        ability("At the beginning of the upkeep of enchanted creature's controller, draw a card.")
            .binding shouldBe TriggerBinding.ATTACHED
        ability("At the beginning of the end step of enchanted creature's controller, draw a card.")
            .trigger shouldBe SdkTriggers.phase(Step.END, Player.You, TriggerBinding.ATTACHED).event
    }

    // Wizards templates the all-players steps several ways, and which is the majority flips with the
    // step — so the canonical is chosen per step rather than by one template with the step slotted.
    "the all-players spellings are one model, and the majority prints" {
        spelledAlso(
            "At the beginning of each player's upkeep, draw a card.",
            "At the beginning of each upkeep, draw a card.",
        )
        spelledAlso(
            "At the beginning of each player's end step, draw a card.",
            "At the beginning of each end step, draw a card.",
        )
        // Pre-2015 templating. Skizzik's golden reads it as `Triggers.EachEndStep`.
        spelledAlso(
            "At the beginning of the end step, draw a card.",
            "At the beginning of each end step, draw a card.",
        )
        // …and where only the possessive form is ever printed, it is the canonical one.
        roundTrips("At the beginning of each player's draw step, draw a card.")
    }

    // Two spellings of `Player.You` that differ in both halves of the template, so they are siblings
    // rather than a derivation of one from the other.
    "the main phases carry both of their printed names" {
        spelledAlso(
            "At the beginning of your precombat main phase, draw a card.",
            "At the beginning of your first main phase, draw a card.",
        )
        spelledAlso(
            "At the beginning of each of your postcombat main phases, draw a card.",
            "At the beginning of your second main phase, draw a card.",
        )
    }

    // The effect clause is the whole step vocabulary, exactly as the event triggers' is: the prefix
    // became a slot without the sentence below it changing at all.
    "every step spelling prints what it parses" {
        listOf(
            "At the beginning of your upkeep, draw a card.",
            "At the beginning of your draw step, draw a card.",
            "At the beginning of your end step, you gain 2 life.",
            "At the beginning of your first main phase, scry 1.",
            "At the beginning of your second main phase, scry 1.",
            "At the beginning of each upkeep, ~ deals 1 damage to any target.",
            "At the beginning of each end step, draw a card.",
            "At the beginning of each combat, draw a card.",
            "At the beginning of each player's draw step, draw a card.",
            "At the beginning of each player's first main phase, draw a card.",
            "At the beginning of each opponent's upkeep, you gain 1 life.",
            "At the beginning of each opponent's end step, you gain 1 life.",
            "At the beginning of each opponent's draw step, you gain 1 life.",
            "At the beginning of the chosen player's upkeep, you gain 1 life.",
            "At the beginning of enchanted player's upkeep, you gain 1 life.",
            "At the beginning of combat on your turn, target creature gets +1/+1 until end of turn.",
            "At the beginning of combat on each opponent's turn, draw a card.",
            "At the beginning of the upkeep of enchanted creature's controller, draw a card.",
            "At the beginning of the end step of enchanted creature's controller, draw a card.",
            "At the beginning of your upkeep, if ~ is in your graveyard, draw a card.",
        ).forEach(::roundTrips)
    }

    // The zone rider and the step vocabulary are independent axes of one rule, so widening either
    // reaches the other. Ghastly Remains is the only card that prints this today.
    "the graveyard-zoned prefix takes the same step vocabulary" {
        ability("At the beginning of each end step, if ~ is in your graveyard, draw a card.")
            .trigger shouldBe SdkTriggers.phase(Step.END, Player.Each).event
    }

    // The band's write-offs, asserted so they stay declines rather than drifting into a half-reading.
    //
    // The attachment *noun* is printed shape decided by an Aura's `enchant` line, and `normalize/`
    // canonicalizes only the adjective — so a non-creature Aura declines rather than being read as a
    // creature one. "The next end step" is a `CreateDelayedTriggerEffect`, a clause inside a sentence
    // rather than a line prefix, and reading it as an ordinary step trigger would mean a permanent
    // that sacrifices itself every turn.
    "the band's write-offs decline rather than being approximated" {
        declines("At the beginning of the upkeep of enchanted land's controller, draw a card.")
        declines("At the beginning of the next end step, draw a card.")
        declines("At the beginning of your next upkeep, draw a card.")
        // No card prints a possessive combat step; `Step.BEGIN_COMBAT` is reachable only through the
        // clause Oracle actually uses, which is what stops the possessive frame inventing one.
        declines("At the beginning of your combat, draw a card.")
    }
})
