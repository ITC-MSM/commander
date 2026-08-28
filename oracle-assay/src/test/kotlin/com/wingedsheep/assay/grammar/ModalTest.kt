package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * "Choose one —" and its rows: the header vocabulary, the lift that makes every effect sentence a
 * mode, and the two disjointness rules that keep four English phrasings printing four models.
 */
class ModalTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    fun declines(line: String) {
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    val chooseOne = "Choose one —\n• Draw a card.\n• You gain 2 life."

    "a modal spell is one script whose effect is a ModalEffect" {
        fragment(chooseOne) shouldBe CardFragment(
            script = CardScript(
                spellEffect = ModalEffect.chooseOne(
                    Mode(Effects.DrawCards(1)),
                    Mode(Effects.GainLife(2)),
                )
            )
        )
        roundTrips(chooseOne)
    }

    // The lift: modes slot the same sentence rule every other context does, so a mode's targets are
    // the sentence's targets and land on the mode rather than on the card.
    "a mode carries the targets its own sentence declared" {
        fragment("Choose one —\n• Destroy target artifact.\n• Draw a card.") shouldBe CardFragment(
            script = CardScript(
                spellEffect = ModalEffect.chooseOne(
                    Mode(
                        Effects.Destroy(Targets.bound()),
                        listOf(Targets.permanent(GameObjectFilter.Artifact)),
                    ),
                    Mode(Effects.DrawCards(1)),
                )
            )
        )
        roundTrips("Choose one —\n• Destroy target artifact.\n• Draw a card.")
    }

    // The whole point of putting the family at a clause position rather than making it a line rule.
    "the same block reads inside a trigger and inside an activated ability" {
        roundTrips("When ~ enters, choose one —\n• Draw a card.\n• You gain 2 life.")
        roundTrips("{2}, {T}: Choose one —\n• Draw a card.\n• You gain 2 life.")
    }

    "the header says how many, and each phrasing says it once" {
        fun modal(line: String) = fragment(line).script.spellEffect as ModalEffect

        modal(chooseOne).let { it.chooseCount shouldBe 1; it.minChooseCount shouldBe 1 }
        modal("Choose two —\n• Draw a card.\n• You gain 2 life.\n• You lose 2 life.")
            .let { it.chooseCount shouldBe 2; it.minChooseCount shouldBe 2 }
        modal("Choose one or both —\n• Draw a card.\n• You gain 2 life.")
            .let { it.chooseCount shouldBe 2; it.minChooseCount shouldBe 1 }
        modal("Choose one or more —\n• Draw a card.\n• You gain 2 life.\n• You lose 2 life.")
            .let { it.chooseCount shouldBe 3; it.minChooseCount shouldBe 1 }

        roundTrips("Choose two —\n• Draw a card.\n• You gain 2 life.\n• You lose 2 life.")
        roundTrips("Choose one or both —\n• Draw a card.\n• You gain 2 life.")
        roundTrips("Choose one or more —\n• Draw a card.\n• You gain 2 life.\n• You lose 2 life.")
    }

    // Disjoint by mode count rather than by alternation order: at two modes the two phrasings would
    // denote the identical (2, 1), which is ambiguity by construction.
    "\"one or both\" and \"one or more\" cannot overlap" {
        declines("Choose one or more —\n• Draw a card.\n• You gain 2 life.")
        declines("Choose one or both —\n• Draw a card.\n• You gain 2 life.\n• You lose 2 life.")
    }

    "a block needs at least two modes" {
        declines("Choose one —\n• Draw a card.")
    }

    // Fail-closed: `Mode` has fields the printed row above does not say, and a matcher that read only
    // the ones it expected would print this card's text for a different card's model.
    "a mode carrying something the row does not say refuses to print" {
        val authored = CardFragment(
            script = CardScript(
                spellEffect = ModalEffect.chooseOne(
                    Mode(Effects.DrawCards(1), description = "Draw a card, but say so differently"),
                    Mode(Effects.GainLife(2)),
                )
            )
        )
        Grammar.abilityLine.printLine(authored) shouldBe null

        val perModeCost = CardFragment(
            script = CardScript(
                spellEffect = ModalEffect.chooseOne(
                    Mode(Effects.DrawCards(1), additionalManaCost = "{1}"),
                    Mode(Effects.GainLife(2)),
                )
            )
        )
        Grammar.abilityLine.printLine(perModeCost) shouldBe null
    }

    // …and the same for the fields on the modal itself, which say which modes may be picked rather
    // than how many. Those are a different question and no header here answers it.
    "a modal carrying a field no header spells refuses to print" {
        val notYetChosen = CardFragment(
            script = CardScript(
                spellEffect = ModalEffect.chooseOneNotYetChosen(
                    Mode(Effects.DrawCards(1)),
                    Mode(Effects.GainLife(2)),
                )
            )
        )
        Grammar.abilityLine.printLine(notYetChosen) shouldBe null
    }

    // (1, 0) — the fifth header. Declining every mode is legal, and the ability leaves the stack
    // having done nothing (CR 700.2b). Disjoint from every other header by `minChooseCount` alone,
    // so it needs no mode-count guard.
    "choose up to one is minChooseCount 0" {
        val upToOne = "Choose up to one —\n• Draw a card.\n• You gain 2 life."
        fragment(upToOne) shouldBe CardFragment(
            script = CardScript(
                spellEffect = ModalEffect(
                    modes = listOf(Mode(Effects.DrawCards(1)), Mode(Effects.GainLife(2))),
                    chooseCount = 1,
                    minChooseCount = 0,
                )
            )
        )
        roundTrips(upToOne)
    }

    // …and it stays disjoint at three modes, where "one or more" also spells a min of 1.
    "choose up to one and choose one or more do not collide at three modes" {
        roundTrips("Choose up to one —\n• Draw a card.\n• You gain 2 life.\n• You lose 2 life.")
        roundTrips("Choose one or more —\n• Draw a card.\n• You gain 2 life.\n• You lose 2 life.")
    }

    // A mode is a sentence, not a step, so nothing nests — and the rule is constructible because of
    // it. Reading this would mean the cascade reached itself.
    "a mode is not itself modal" {
        declines("Choose one —\n• Choose one —\n• Draw a card.\n• You gain 2 life.\n• You lose 2 life.")
    }
})
