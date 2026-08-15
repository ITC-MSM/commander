package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.dsl.Costs as SdkCosts
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The cost-colon-effect sentence, and the mana vocabulary that is nearly all of it — the rules that
 * make a land parse whole.
 */
class ActivatedTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    fun manaAbility(cost: AbilityCost, color: Color, amount: Int = 1) = ActivatedAbility(
        id = AbilityId("activated"),
        cost = cost,
        effect = Effects.AddMana(color, amount),
        timing = TimingRule.ManaAbility,
        isManaAbility = true,
    )

    // Llanowar Elves' golden, and 472 others: the whole model is cost, effect, and the two fields
    // that say it is a mana ability.
    "a tap-for-mana line is the ability a hand-written card carries" {
        fragment("{T}: Add {G}.") shouldBe CardFragment(
            script = CardScript(activatedAbilities = listOf(manaAbility(AbilityCost.Tap, Color.GREEN)))
        )
        roundTrips("{T}: Add {G}.")
    }

    // Jungle Hollow's golden is two CostTap entries, one BLACK and one GREEN — CR-shorthand for two
    // abilities, the same shape `Keywords.qualityRun` gives multi-quality protection.
    "a dual land's line is two abilities sharing one cost" {
        fragment("{T}: Add {B} or {G}.") shouldBe CardFragment(
            script = CardScript(
                activatedAbilities = listOf(
                    manaAbility(AbilityCost.Tap, Color.BLACK),
                    manaAbility(AbilityCost.Tap, Color.GREEN),
                )
            )
        )
        roundTrips("{T}: Add {B} or {G}.")
        roundTrips("{T}: Add {W}, {U}, or {B}.")
    }

    "the cost vocabulary round-trips over the shapes a mana ability prints" {
        listOf(
            "{T}: Add {C}.",
            "{T}: Add {G}{G}.",
            "{T}: Add {C}{C}{C}.",
            "{1}: Add {B}.",
            "{2}, {T}: Add {R}.",
            "{2}{R}, {T}: Add {W} or {U}.",
        ).forEach { roundTrips(it) }

        fragment("{2}, {T}: Add {R}.").script.activatedAbilities.single().cost shouldBe
            SdkCosts.Composite(SdkCosts.Mana("{2}"), AbilityCost.Tap)
    }

    // The whole reason the effect clause is a Steps slot: every step rule reaches this sentence
    // without this file knowing any of them.
    "the clause after the colon is the same grammar a spell prints" {
        listOf(
            "{T}: Draw a card.",
            "{2}: Target creature gets +1/+1 until end of turn.",
            "{1}, {T}: Tap target creature.",
            "{T}: You gain 2 life.",
        ).forEach { roundTrips(it) }

        fragment("{1}, {T}: Tap target creature.").script.activatedAbilities.single()
            .targetRequirements shouldBe listOf(Targets.permanent(GameObjectFilter.Creature))
    }

    // CR 605.1a: no target, and it could add mana. Both flags are derived rather than spelled,
    // because no printed word says either.
    "mana-ability-ness is derived from the effect, not from the sentence" {
        fragment("{T}: Add {G}.").script.activatedAbilities.single().isManaAbility shouldBe true
        fragment("{T}: Draw a card.").script.activatedAbilities.single().isManaAbility shouldBe false
        fragment("{T}: Draw a card.").script.activatedAbilities.single().timing shouldBe
            TimingRule.InstantSpeed
    }

    // The fail-closed half. An `ActivatedAbility` has two dozen fields the sentence does not spell,
    // and printing one that carries any of them would drop it and still round-trip.
    "an ability carrying content the sentence does not spell refuses to print" {
        val restricted = CardFragment(
            script = CardScript(
                activatedAbilities = listOf(
                    manaAbility(AbilityCost.Tap, Color.GREEN)
                        .copy(restrictions = listOf(ActivationRestriction.Once))
                )
            )
        )
        Grammar.abilityLine.printLine(restricted) shouldBe null

        val relabelled = CardFragment(
            script = CardScript(
                activatedAbilities = listOf(
                    manaAbility(AbilityCost.Tap, Color.GREEN).copy(descriptionOverride = "{T}: Add {G}.")
                )
            )
        )
        Grammar.abilityLine.printLine(relabelled) shouldBe null
    }

    // A run of *different* symbols is a composite effect the SDK spells another way; reading the
    // first symbol and dropping the rest would round-trip and mean something else.
    "a mixed run of symbols declines rather than reading the first one" {
        Grammar.abilityLine.parseLine("{T}: Add {W}{U}.").shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    // The differential's answer for these is `ManaColorSet.Specific`, which the grammar deliberately
    // never emits — registering both would be one text with two models.
    "the choice form is several abilities and never a colour set" {
        fragment("{T}: Add {B} or {G}.").script.activatedAbilities.map { it.effect } shouldBe
            listOf(Effects.AddMana(Color.BLACK), Effects.AddMana(Color.GREEN))
    }

    // Dark Ritual: producing mana is a spell effect in its own right, which is why the rule lives in
    // Steps rather than only behind a cost.
    "adding mana is a spell line as well as an ability's effect" {
        fragment("Add {B}{B}{B}.") shouldBe
            CardFragment(script = CardScript(spellEffect = Effects.AddMana(Color.BLACK, 3)))
        roundTrips("Add {B}{B}{B}.")
    }
})
