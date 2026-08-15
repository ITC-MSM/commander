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
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The subtype layer — the noun phrase a tribal set is built out of, and the three traps it walks
 * past.
 *
 * A subtype is the first thing [Filters] reads that is a *proper noun*, and that is what makes it
 * worth its own file: the word can stand at a sentence start where
 * [com.wingedsheep.assay.syntax.SentenceCase] has already lowercased it, it can be spelled with or
 * without its type noun, and an ungated reading of it collides with the enumerated type list. Each
 * of those is asserted here.
 */
class SubtypesTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    val slivers = GameObjectFilter.Creature.withSubtype("Sliver")

    "a subtype in front of a type noun is a predicate on that noun" {
        fragment("Sliver creatures get +1/+0.") shouldBe CardFragment(
            script = CardScript(staticAbilities = listOf(ModifyStats(1, 0, GroupFilter(slivers))))
        )
        roundTrips("Sliver creatures get +1/+0.")
        roundTrips("Cleric creatures have vigilance.")
    }

    // The adjective does not inflect: only the head noun does. "Slivers creatures" is not English
    // and must not be a reading.
    "the adjective stays singular in both numbers" {
        roundTrips("Target Sliver creature gets +1/+1 until end of turn.")
        Grammar.abilityLine
            .parseLine("Slivers creatures get +1/+0.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    // A sentence start is decapitalized before the grammar sees it, so the leaf has to read the
    // lowercased spelling — otherwise every lord line in Magic declines on its first word.
    "a subtype at a sentence start reads through the lowercasing" {
        fragment("Sliver creatures have flying.") shouldBe CardFragment(
            script = CardScript(staticAbilities = listOf(GrantKeyword(Keyword.FLYING, GroupFilter(slivers))))
        )
        roundTrips("Goblin creatures get +2/+0 until end of turn.")
    }

    // …and it may only do so for a word the SDK names as a type. "Other" is capitalized at a
    // sentence start and is not a tribe; reading it as one round-trips byte-perfectly and means a
    // creature type Magic does not have, which is the reversible-but-wrong class exactly.
    "a common word at a sentence start is not a creature type" {
        fragment("Other creatures you control get +0/+1.") shouldBe CardFragment(
            script = CardScript(
                staticAbilities = listOf(
                    ModifyStats(0, 1, GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true))
                )
            )
        )
        roundTrips("Other creatures you control get +0/+1.")
    }

    // The gate on the lowercase reading is also what keeps the enumerated type list unambiguous:
    // "artifact creature" is one value, not that value *and* creatures of a type called Artifact.
    "an enumerated type phrase keeps exactly one reading" {
        val line = "Destroy target artifact creature."
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>()
        roundTrips(line)
    }

    // The bare noun denotes what the adjective form denotes, so exactly one of them may print. The
    // adjective is canonical because it says what the model says; the bare one comes back as a
    // variant rather than as a decline.
    "the bare noun parses and prints back as the adjective form" {
        fragment("Slivers can't block.") shouldBe fragment("Sliver creatures can't block.")
        Grammar.abilityLine.printLine(fragment("Slivers can't block.")) shouldBe
            "Sliver creatures can't block."
    }

    // A bare noun has to *imply* "creature", which is a guess — so it is offered only for words the
    // SDK's creature-type list names. A basic land type would otherwise have two readings.
    "a bare land type is a land, not a creature of that type" {
        fragment("Sacrifice a Forest: Draw a card.") shouldBe fragment("Sacrifice a Forest: Draw a card.")
        Grammar.abilityLine
            .parseLine("Sacrifice a Forest: Draw a card.")
            .shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>()
    }

    "the negated subtype is its own layer, hyphenated the way Oracle writes it" {
        roundTrips("Destroy target non-Zombie creature.")
    }
})
