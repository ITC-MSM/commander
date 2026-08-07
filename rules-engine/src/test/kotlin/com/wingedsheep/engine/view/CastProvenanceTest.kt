package com.wingedsheep.engine.view

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.sdk.core.Zone
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The two renderings of a cast's provenance. Both read off the same (alternative cost, origin zone)
 * pair, so this pins that they stay consistent with each other and that an ordinary cast from hand
 * stays silent rather than adding noise to every log line.
 */
class CastProvenanceTest : FunSpec({

    test("a plain cast from hand has no provenance to report") {
        CastProvenance.logPhrase(null, Zone.HAND) shouldBe null
        CastProvenance.badgeLabel(null, Zone.HAND) shouldBe null
    }

    test("an unresolved origin zone with no alternative cost reports nothing") {
        CastProvenance.logPhrase(null, null) shouldBe null
        CastProvenance.badgeLabel(null, null) shouldBe null
    }

    test("a disturb cast names both the mechanic and the graveyard it came from") {
        CastProvenance.logPhrase(AlternativeCostType.DISTURB, Zone.GRAVEYARD) shouldBe
            "disturb, from graveyard"
        CastProvenance.badgeLabel(AlternativeCostType.DISTURB, Zone.GRAVEYARD) shouldBe
            "Disturb · Graveyard"
    }

    test("an alternative cost paid from hand names only the mechanic") {
        CastProvenance.logPhrase(AlternativeCostType.EVOKE, Zone.HAND) shouldBe "evoke"
        CastProvenance.badgeLabel(AlternativeCostType.EVOKE, Zone.HAND) shouldBe "Evoke"
    }

    test("a normal cast from a zone other than hand names the zone alone") {
        CastProvenance.logPhrase(null, Zone.COMMAND) shouldBe "from command zone"
        CastProvenance.badgeLabel(null, Zone.COMMAND) shouldBe "Command zone"
        CastProvenance.logPhrase(null, Zone.EXILE) shouldBe "from exile"
    }

    test("every alternative cost has a player-facing name") {
        // Guards the exhaustive `when` against a new mechanic slipping through as a blank badge.
        AlternativeCostType.entries.forEach { type ->
            val phrase = CastProvenance.logPhrase(type, Zone.HAND)
            io.kotest.assertions.withClue("no name for $type") {
                (phrase != null && phrase.isNotBlank()) shouldBe true
            }
        }
    }
})
