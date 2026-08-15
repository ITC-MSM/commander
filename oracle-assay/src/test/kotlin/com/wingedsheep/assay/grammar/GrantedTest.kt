package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Abilities handed to a whole group — the two ways Magic prints one, and the reading that must not
 * escape the second of them.
 *
 * A grant is printed **quoted** ("All Slivers have "{T}: Regenerate target Sliver."") or **unquoted**
 * ("Whenever a Sliver deals combat damage to a player, its controller may draw a card."). The quoted
 * form is written from the gaining permanent's point of view and says "you"; the unquoted one is
 * written from the granting card's and says "its controller". Same model, two subjects.
 */
class GrantedTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    // The quoted form slots the whole activated-ability grammar, so nothing about the ability inside
    // the quotes is restated here.
    "a quoted activated ability is the ordinary ability grammar inside quotation marks" {
        roundTrips("Sliver creatures have \"{T}: Regenerate target Sliver creature.\"")
        roundTrips("Sliver creatures have \"{2}: ~ gets +1/+1 until end of turn.\"")
    }

    "an unquoted grant reads as a static whose filter is the group, not as a trigger" {
        roundTrips("Whenever a Sliver creature deals combat damage to a player, its controller may draw a card.")
        roundTrips("Whenever a Sliver creature deals damage, its controller gains that much life.")
    }

    // The reading that must not escape: outside a grant, "its controller" is the controller of
    // whatever the sentence was just talking about — a different player. Registering the spelling
    // globally would read "Whenever a creature dies, its controller loses 1 life" as a sentence
    // about *you*, which round-trips byte-perfectly and means the wrong thing.
    "the third-person subject is not readable outside a grant" {
        Grammar.abilityLine
            .parseLine("Its controller may draw a card.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
        Grammar.abilityLine
            .parseLine("When ~ enters, its controller may draw a card.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    // "May" lowers into the granted ability's own `optional` flag, exactly as it does on a card's
    // own trigger — the two spellings are one sentence written inside and outside a grant.
    "a granted may-clause lowers into the ability's optional flag" {
        fragment("Whenever a Sliver creature deals combat damage to a player, its controller may draw a card.")
            .script.staticAbilities.size shouldBe 1
    }
})
