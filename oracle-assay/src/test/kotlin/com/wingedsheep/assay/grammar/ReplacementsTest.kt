package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.IsYourTurn
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** "This land enters tapped." — the self-replacement on a permanent's own entry. */
class ReplacementsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    // 234 hand-written cards carry exactly this, the bare default.
    "the tapped-entry line is the replacement effect the goldens carry" {
        fragment("~ enters tapped.") shouldBe
            CardFragment(script = CardScript(replacementEffects = listOf(EntersTapped())))
        roundTrips("~ enters tapped.")
    }

    // Steam Vents and the other shock lands: the same type with one field set, which is why it is a
    // row beside the plain rule rather than a family of its own.
    "the shock-land sentence is the same type with a life cost" {
        fragment("As ~ enters, you may pay 2 life. If you don't, it enters tapped.") shouldBe
            CardFragment(script = CardScript(replacementEffects = listOf(EntersTapped(payLifeCost = 2))))
        roundTrips("As ~ enters, you may pay 2 life. If you don't, it enters tapped.")
    }

    // The check lands. The condition is a slot rather than a rule, so the whole of `Conditions`
    // arrives at once — the four sentences below are one template with four fillings.
    "a conditional tapped entry is the type's third field, filled from the condition vocabulary" {
        fragment("~ enters tapped unless you control a basic land.") shouldBe CardFragment(
            script = CardScript(
                replacementEffects = listOf(
                    EntersTapped(unlessCondition = Conditions.YouControl(GameObjectFilter.BasicLand))
                )
            )
        )
        roundTrips("~ enters tapped unless you control a basic land.")
        roundTrips("~ enters tapped unless a player has 13 or less life.")
        roundTrips("~ enters tapped unless you control two or more basic lands.")
    }

    // Two articles make two noun phrases with the verb elided, which is a disjunction of
    // *conditions*; one article makes one noun phrase, which is a disjunction inside the filter.
    // The goldens draw the line in the same place — Sulfur Falls holds `Any(Exists, Exists)`.
    "the check lands' two-article disjunction is a condition, not a filter" {
        fragment("~ enters tapped unless you control an Island or a Mountain.") shouldBe CardFragment(
            script = CardScript(
                replacementEffects = listOf(
                    EntersTapped(
                        unlessCondition = Conditions.Any(
                            Conditions.YouControl(GameObjectFilter.Land.withSubtype("Island")),
                            Conditions.YouControl(GameObjectFilter.Land.withSubtype("Mountain")),
                        )
                    )
                )
            )
        )
        roundTrips("~ enters tapped unless you control an Island or a Mountain.")
    }

    // "Other" is `AggregateBattlefield.excludeSelf`, not one-higher arithmetic over the whole
    // group: the two agree only while the source itself matches the filter. Twenty hand-written
    // lands spelled the shortcut and moved to this reading in the change that added the rule.
    "the fast and slow lands count OTHER lands, and the word is a field on the amount" {
        fragment("~ enters tapped unless you control two or fewer other lands.") shouldBe CardFragment(
            script = CardScript(
                replacementEffects = listOf(
                    EntersTapped(
                        unlessCondition = Conditions.YouControlOtherAtMost(2, GameObjectFilter.Land)
                    )
                )
            )
        )
        roundTrips("~ enters tapped unless you control two or fewer other lands.")
        roundTrips("~ enters tapped unless you control two or more other lands.")
        roundTrips("~ enters tapped unless you control three or more other Islands.")
    }

    // The reconstruct-and-compare in the `match` half: a value carrying a field this sentence has
    // no room for refuses to print rather than dropping it.
    "a tapped entry that also costs life refuses to print as the conditional sentence" {
        val both = CardFragment(
            script = CardScript(
                replacementEffects = listOf(
                    EntersTapped(unlessCondition = IsYourTurn, payLifeCost = 2)
                )
            )
        )
        Grammar.abilityLine.printLine(both) shouldBe null
    }

    // A condition the SDK cannot name still declines, and is counted rather than approximated —
    // "you have two or more opponents" is ten corpus cards with no facade to build through. The turn
    // clause declines for the opposite reason: the SDK names it, and `SpellCosts.leadingGate`
    // already owns its one printed form.
    "a condition outside the vocabulary declines rather than losing itself" {
        Grammar.abilityLine.parseLine("~ enters tapped unless you have two or more opponents.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
        Grammar.abilityLine.parseLine("~ enters tapped unless it's your turn.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
    }
})
