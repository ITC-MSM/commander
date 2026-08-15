package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The first rules that produce a `CardScript` rather than a keyword — the start of the pipeline
 * family, and the point where the line model had to widen to [CardFragment].
 */
class StepsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    "a bare draw is the spell effect and takes no target" {
        fragment("Draw two cards.") shouldBe
            CardFragment(script = CardScript(spellEffect = Effects.DrawCards(2)))
        roundTrips("Draw two cards.")
    }

    "the singular is its own rule, so it prints as an article rather than a number" {
        fragment("Draw a card.") shouldBe
            CardFragment(script = CardScript(spellEffect = Effects.DrawCards(1)))
        roundTrips("Draw a card.")
    }

    "a targeted draw declares the requirement and binds the effect to it" {
        fragment("Target player draws two cards.") shouldBe CardFragment(
            script = CardScript(
                spellEffect = Effects.DrawCards(2, Targets.bound()),
                targetRequirements = listOf(Targets.player()),
            )
        )
        roundTrips("Target player draws two cards.")
        roundTrips("Target player draws a card.")
    }

    "every number word the vocabulary spells round-trips" {
        listOf("two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
            .forEach { roundTrips("Draw $it cards.") }
    }

    // Singular and plural must not overlap, or every draw card in the corpus reports AMBIGUOUS.
    // "Draw one cards." is the shape that would prove they do.
    "the plural rule refuses one, so exactly one surface form exists per count" {
        Grammar.abilityLine.parseLine("Draw one cards.").shouldBeInstanceOf<ParseOutcome.Declined>()
        Grammar.abilityLine.parseLine("Draw one card.").shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    "a count past the vocabulary declines rather than printing a form nobody uses" {
        Grammar.abilityLine.parseLine("Draw twenty cards.").shouldBeInstanceOf<ParseOutcome.Declined>()
        Grammar.abilityLine.printLine(
            CardFragment(script = CardScript(spellEffect = Effects.DrawCards(20)))
        ) shouldBe null
    }

    // The fail-closed half of every `match`: a script carrying more than the rule inspected must not
    // print, or the extra content is dropped and the line still round-trips — reversible, wrong.
    "a script with content the rule did not inspect refuses to print" {
        val withExtra = CardFragment(
            script = CardScript(
                spellEffect = Effects.DrawCards(2),
                triggeredAbilities = emptyList(),
                cantBeCountered = true,
            )
        )

        Grammar.abilityLine.printLine(withExtra) shouldBe null
    }

    "a targeted draw and a bare draw are different models, not two spellings of one" {
        fragment("Draw two cards.") shouldBe
            CardFragment(script = CardScript(spellEffect = Effects.DrawCards(2)))
        (fragment("Target player draws two cards.") == fragment("Draw two cards.")) shouldBe false
    }

    "a keyword line and a spell line stay distinguishable in the same alternation" {
        fragment("Flying").script shouldBe CardScript.EMPTY
        fragment("Draw a card.").keywordAbilities shouldBe emptyList()
    }

    // Murder's golden, written by hand, is exactly this model — which is the point of the
    // differential: the grammar has to land on what a person wrote from the same sentence.
    "destroying a targeted permanent declares the requirement the card declares" {
        fragment("Destroy target creature.") shouldBe CardFragment(
            script = CardScript(
                spellEffect = Effects.Destroy(Targets.bound()),
                targetRequirements = listOf(Targets.permanent(GameObjectFilter.Creature)),
            )
        )
        roundTrips("Destroy target creature.")
    }

    "every verb in the family round-trips over the filter vocabulary" {
        listOf(
            "Destroy target artifact.",
            "Destroy target artifact or enchantment.",
            "Destroy target creature or planeswalker.",
            "Destroy target nonland permanent.",
            "Exile target creature.",
            "Exile target permanent.",
            "Tap target creature.",
            "Untap target artifact.",
            "Return target creature to its owner's hand.",
            "Return target permanent to its owner's hand.",
        ).forEach { roundTrips(it) }
    }

    "the controller clause is a suffix on the model as well as on the sentence" {
        fragment("Destroy target creature you control.") shouldBe CardFragment(
            script = CardScript(
                spellEffect = Effects.Destroy(Targets.bound()),
                targetRequirements = listOf(Targets.permanent(GameObjectFilter.Creature.youControl())),
            )
        )
        roundTrips("Destroy target creature you control.")
        roundTrips("Destroy target creature an opponent controls.")
        roundTrips("Tap target artifact you control.")
    }

    // Fail-closed the other way: a requirement carrying a restriction the phrase does not spell
    // must not print as though it did. `excludeSelf` is "other target creature", a different card.
    "a target requirement the phrase does not spell refuses to print" {
        val other = CardFragment(
            script = CardScript(
                spellEffect = Effects.Destroy(Targets.bound()),
                targetRequirements = listOf(
                    TargetPermanent(
                        filter = TargetFilter(GameObjectFilter.Creature, excludeSelf = true),
                        id = Targets.SLOT,
                    )
                ),
            )
        )

        Grammar.abilityLine.printLine(other) shouldBe null
    }
})
