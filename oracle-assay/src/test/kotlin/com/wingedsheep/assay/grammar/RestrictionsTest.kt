package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.CastRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.YouWereAttackedThisStep
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The lines and clauses that constrain rather than do — Portal's combat tricks and its
 * before-attackers activated abilities, and the additional cost a spell states on a line of its own.
 *
 * These are the first fragments carrying no effect at all, which is why they are lines rather than
 * clauses: nothing in [Steps] could hold them.
 */
class RestrictionsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    // Two restrictions joined by "and", and the model is a list of two — which is why the rule is a
    // run rather than a rule per printed combination.
    "a casting restriction line is a run over one vocabulary" {
        fragment(
            "Cast this spell only during the declare attackers step and only if you've been attacked this step."
        ) shouldBe CardFragment(
            script = CardScript(
                castRestrictions = listOf(
                    CastRestriction.OnlyDuringStep(Step.DECLARE_ATTACKERS),
                    CastRestriction.OnlyIfCondition(YouWereAttackedThisStep),
                )
            )
        )
        roundTrips(
            "Cast this spell only during the declare attackers step and only if you've been attacked this step."
        )
        roundTrips("Cast this spell only during the declare attackers step.")
    }

    "an additional cost is a line of its own" {
        fragment("As an additional cost to cast this spell, sacrifice a creature.") shouldBe CardFragment(
            script = CardScript(
                additionalCosts = listOf(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))
            )
        )
        roundTrips("As an additional cost to cast this spell, sacrifice a creature.")
        roundTrips("As an additional cost to cast this spell, sacrifice a green creature.")
    }

    // The spell side of the shared atom vocabulary. None of these has a rule of its own here: they
    // are rows written for an activated ability's cost, and this line reaches them because both
    // contexts slot the same `Phrase<CostAtom>` — which is the whole argument for that shape.
    "every cost atom reaches the additional-cost line, lowercased, without a rule of its own" {
        fragment("As an additional cost to cast this spell, discard a card.") shouldBe CardFragment(
            script = CardScript(additionalCosts = listOf(Costs.additional.DiscardCards()))
        )
        roundTrips("As an additional cost to cast this spell, discard a card.")
        roundTrips("As an additional cost to cast this spell, discard two cards.")
        roundTrips("As an additional cost to cast this spell, pay 3 life.")
        roundTrips("As an additional cost to cast this spell, exile a creature card from your graveyard.")
        roundTrips("As an additional cost to cast this spell, return a land you control to its owner's hand.")
        roundTrips("As an additional cost to cast this spell, tap an untapped Vampire creature you control.")
    }

    // The other half of the split: a spell being cast has no source permanent, so the costs that
    // *are* the source stay on the activation side and this line cannot spell them.
    "the costs only a permanent can pay are unreachable from the additional-cost line" {
        Grammar.abilityLine.parseLine("As an additional cost to cast this spell, sacrifice ~.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    // The activation restriction is a *sentence* after the ability's own, joined by a comma rather
    // than by "and" — a printed-shape difference from the casting line, so each has its separator.
    "an activated ability carries its restrictions in a trailing sentence" {
        val line = "{T}: Destroy target tapped creature. Activate only during your turn, " +
            "before attackers are declared."
        val ability = fragment(line).script.activatedAbilities.single()
        ability.restrictions shouldBe listOf(
            ActivationRestriction.OnlyDuringYourTurn,
            ActivationRestriction.BeforeStep(Step.DECLARE_ATTACKERS),
        )
        roundTrips(line)
    }

    // The unrestricted rule and the restricted one take disjoint models, so the model decides which
    // prints rather than the alternation's order.
    "an unrestricted ability still prints without the sentence" {
        roundTrips("{T}: Destroy target tapped creature.")
    }

    // A condition the SDK cannot name declines rather than being approximated by the nearest one.
    // The fixture used to be "only if you control a Forest", which [Conditions] now reads through
    // the general `YouControl` facade; a condition with no facade entry at all takes its place.
    "a restriction the vocabulary does not name declines" {
        Grammar.abilityLine
            .parseLine("Cast this spell only if you've drawn a card this turn.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
    }
})
