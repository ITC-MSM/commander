package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * A grant clause names a *run* of keywords, and the count lives in the slot rather than in the rule.
 *
 * The property that matters is the one the count-in-the-rule shape could not have: one grant is the
 * bare effect and several are a composite, in every grant position — target, group, source, and the
 * enchanted permanent — from a single vocabulary. The negative cases are the ones that keep printing
 * determined: a one-element composite is not what a single grant means, and a run of one must not be
 * reachable from the static *line* rule, where the single-ability rule already spells it.
 */
class KeywordRunsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    fun declines(line: String) {
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    "a grant to a target is one effect for one keyword and a composite for several" {
        fragment("Target creature gains flying until end of turn.").script.spellEffect shouldBe
            Effects.GrantKeyword(Keyword.FLYING, Targets.bound())

        (fragment("Target creature gains flying and trample until end of turn.")
            .script.spellEffect as CompositeEffect).effects shouldBe listOf(
            Effects.GrantKeyword(Keyword.FLYING, Targets.bound()),
            Effects.GrantKeyword(Keyword.TRAMPLE, Targets.bound()),
        )

        roundTrips("Target creature gains flying until end of turn.")
        roundTrips("Target creature gains flying and trample until end of turn.")
        roundTrips("Target creature gains flying, trample, and lifelink until end of turn.")
    }

    // Overprotect and Scales of Shale — the pump keeps its place at the head of the composite and
    // every keyword after it is its own grant.
    "a pump and a run share one target and one composite" {
        (fragment("Target creature you control gets +3/+3 and gains trample, hexproof, and indestructible until end of turn.")
            .script.spellEffect as CompositeEffect).effects shouldBe listOf(
            Effects.ModifyStats(3, 3, Targets.bound()),
            Effects.GrantKeyword(Keyword.TRAMPLE, Targets.bound()),
            Effects.GrantKeyword(Keyword.HEXPROOF, Targets.bound()),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, Targets.bound()),
        )
        roundTrips("Target creature you control gets +3/+3 and gains trample, hexproof, and indestructible until end of turn.")
        roundTrips("Target creature gets +2/+0 and gains lifelink and indestructible until end of turn.")
    }

    // Sword of Vengeance. The static line denotes one ability per keyword, plus the pump.
    "an equipment's run is one static per keyword" {
        fragment("Enchanted creature gets +2/+0 and has first strike, vigilance, trample, and haste.")
            .script.staticAbilities shouldBe listOf(
            ModifyStats(2, 0),
            GrantKeyword(Keyword.FIRST_STRIKE),
            GrantKeyword(Keyword.VIGILANCE),
            GrantKeyword(Keyword.TRAMPLE),
            GrantKeyword(Keyword.HASTE),
        )
        roundTrips("Enchanted creature gets +2/+0 and has first strike, vigilance, trample, and haste.")
        roundTrips("Enchanted creature gets +1/+1 and has reach and vigilance.")
    }

    "the keyword-only static line takes two or more, and one still goes through the single rule" {
        fragment("Enchanted creature has reach and vigilance.").script.staticAbilities shouldBe
            listOf(GrantKeyword(Keyword.REACH), GrantKeyword(Keyword.VIGILANCE))
        fragment("Enchanted creature has flying.").script.staticAbilities shouldBe
            listOf(GrantKeyword(Keyword.FLYING))
        roundTrips("Enchanted creature has reach and vigilance.")
        roundTrips("Enchanted creature has flying.")
    }

    // Skirk Outrider, and the keyword-only form the same shape gained with the run.
    "a conditional self static carries a run in either printed order" {
        roundTrips("~ gets +2/+2 and has trample as long as you control a Beast permanent.")
        roundTrips("~ has flying and vigilance as long as you control a Beast permanent.")
        // The leading form is the alternate: it parses to the same value and prints as the trailing one.
        fragment("As long as you control a Beast, ~ has flying and vigilance.") shouldBe
            fragment("~ has flying and vigilance as long as you control a Beast permanent.")
    }

    "a group's run iterates once and carries every grant" {
        roundTrips("Creatures you control get +1/+1 and gain vigilance until end of turn.")
        roundTrips("Creatures you control get +1/+1 and gain trample and haste until end of turn.")
    }

    // The trap the shape replaces: a rule that spelled the count would have printed a single grant
    // as a one-element composite, which is not what any hand-written card holds.
    "a one-element composite is not a single grant and refuses to print" {
        val oneElement = CardFragment(
            script = CardScript(
                spellEffect = CompositeEffect(listOf(Effects.GrantKeyword(Keyword.FLYING, Targets.bound()))),
                targetRequirements = listOf(Targets.permanent(GameObjectFilter.Creature)),
            )
        )
        Grammar.abilityLine.printLine(oneElement) shouldBe null
    }

    "a keyword with a parameter is not in the run vocabulary" {
        declines("Target creature gains ward {2} until end of turn.")
    }
})
