package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The aura band: "Enchant creature" and the statics the enchanted creature gets.
 *
 * The three round-trip cases are Holy Strength, Flight and Spectral Flight — the whole of each of
 * those cards. The two refusal cases are the ones that matter: a static whose `GroupFilter` is *not*
 * the aura default must not print as an aura's sentence, or the rule silently prints a lord.
 */
class StaticsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    // Enchant is a keyword ability in CR 702.5 and a `TargetRequirement` in the SDK, which is why it
    // is here and not in Keywords. The requirement is the ordinary filtered target, so the whole of
    // Filters arrives with it.
    "the enchant line is the aura's attachment restriction" {
        fragment("Enchant creature") shouldBe CardFragment(
            script = CardScript(
                auraTarget = TargetPermanent(
                    filter = TargetFilter(GameObjectFilter.Creature),
                    id = Targets.SLOT,
                )
            )
        )
        roundTrips("Enchant creature")
        roundTrips("Enchant land")
        roundTrips("Enchant creature you control")
        roundTrips("Enchant creature an opponent controls")
    }

    // Holy Strength. The golden omits `filter` entirely because the aura form *is* ModifyStats's
    // default, so the rule constructs it the same way.
    "the aura pump is the default-filtered ModifyStats the goldens carry" {
        fragment("Enchanted creature gets +1/+2.") shouldBe
            CardFragment(script = CardScript(staticAbilities = listOf(ModifyStats(1, 2))))
        roundTrips("Enchanted creature gets +1/+2.")
        roundTrips("Enchanted creature gets -3/-0.")
    }

    // Flight. GrantKeyword holds a String, so reading it back has to find the enum constant rather
    // than assume one.
    "the granted keyword is the whole simple-keyword vocabulary" {
        fragment("Enchanted creature has flying.") shouldBe
            CardFragment(script = CardScript(staticAbilities = listOf(GrantKeyword(Keyword.FLYING))))
        roundTrips("Enchanted creature has flying.")
        roundTrips("Enchanted creature has shroud.")
        roundTrips("Enchanted creature has double strike.")
    }

    // Spectral Flight: one sentence, two abilities — the qualityRun / Mana.alternatives shape a
    // third time. The list is the model; there is no compound type.
    "a pump joined to a grant is two static abilities from one sentence" {
        fragment("Enchanted creature gets +2/+2 and has flying.") shouldBe CardFragment(
            script = CardScript(
                staticAbilities = listOf(ModifyStats(2, 2), GrantKeyword(Keyword.FLYING))
            )
        )
        roundTrips("Enchanted creature gets +2/+2 and has flying.")
        roundTrips("Enchanted creature gets +1/+1 and has trample.")
    }

    // The fail-closed half, and the reason the `match` rules reconstruct rather than walk fields.
    // "Creatures you control get +1/+1." is the *same SDK type* with a real GroupFilter; a rule that
    // read only the two bonuses would print it as an aura's line and lose the whole lord clause.
    "a lord's pump refuses to print as an aura's line" {
        val lord = CardFragment(
            script = CardScript(
                staticAbilities = listOf(
                    ModifyStats(1, 1, GroupFilter(GameObjectFilter.Creature).youControl())
                )
            )
        )
        Grammar.abilityLine.printLine(lord) shouldBe null
    }

    // …and the same for the grant, whose default filter is the same one.
    "a keyword granted to a group refuses to print as an aura's line" {
        val anthem = CardFragment(
            script = CardScript(
                staticAbilities = listOf(
                    GrantKeyword(Keyword.FLYING, GroupFilter(GameObjectFilter.Creature).youControl())
                )
            )
        )
        Grammar.abilityLine.printLine(anthem) shouldBe null
    }

    // The noun is in the text and not in the model: `attachedCreature()` says "the thing this is
    // attached to" and nothing about creature-ness, so "Enchanted land" and "Enchanted creature"
    // would denote one value. Exactly one is spelled; the other declines rather than being
    // re-spelled into a sentence the card does not print.
    "a noun the grammar does not spell declines rather than being normalized away" {
        Grammar.abilityLine.parseLine("Enchanted land has flying.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    // Every rule in the family can print what it parses — the meta-test each family gets, because a
    // `match` half that quietly matches nothing compiles, parses, and surfaces as a print mismatch
    // far from its cause.
    "every static rule prints what it parses" {
        val lines = listOf(
            "Enchanted creature gets +1/+2.",
            "Enchanted creature has flying.",
            "Enchanted creature gets +2/+2 and has flying.",
        )
        lines.forEach { line -> Grammar.abilityLine.printLine(fragment(line)) shouldBe line }
    }
})
