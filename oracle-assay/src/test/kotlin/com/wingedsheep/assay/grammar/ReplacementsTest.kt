package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.EntersTapped
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

    // The check lands. An `unlessCondition` is an arbitrary Condition and there is no condition
    // vocabulary yet, so these decline and rank — printing one as "~ enters tapped." would be
    // byte-perfect and a different card.
    "a conditional tapped entry declines in both directions rather than losing its condition" {
        Grammar.abilityLine.parseLine("~ enters tapped unless you control a basic land.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()

        val conditional = CardFragment(
            script = CardScript(
                replacementEffects = listOf(EntersTapped(unlessCondition = IsYourTurn))
            )
        )
        Grammar.abilityLine.printLine(conditional) shouldBe null
    }
})
