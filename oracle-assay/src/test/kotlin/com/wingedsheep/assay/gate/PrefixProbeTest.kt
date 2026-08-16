package com.wingedsheep.assay.gate

import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleFace
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * The half that makes the ranking honest: which lines a band *finishes*, not which cards it reaches.
 */
class PrefixProbeTest : StringSpec({

    fun card(name: String, text: String) = OracleCard(
        name = name,
        oracleId = null,
        layout = "normal",
        setCode = "TST",
        scryfallKeywords = emptyList(),
        faces = listOf(OracleFace(name = name, oracleText = text, typeLine = "Creature — Angel")),
    )

    val touchstone = Touchstone()

    fun keyOf(card: OracleCard) = DeclineKey.TAIL.of(
        touchstone.assay(card).lines.first { it.verdict == LineVerdict.DECLINED }
    )

    "a card is finished only when every one of its declined lines reads after the substitution" {
        // Both cards decline on the same unknown prefix. The first's payload is grammar the parser
        // already has; the second's second line is blocked by something the substitution never
        // touches, which is exactly the difference the card counts hide.
        val readable = card("Readable", "Whenever ~ frobnicates, draw a card.")
        val alsoBlocked = card("Also Blocked", "Whenever ~ frobnicates, draw a card.\nWhenever ~ blorps, draw a card.")

        val result = PrefixProbe.run(
            touchstone = touchstone,
            cards = listOf(readable, alsoBlocked),
            family = DeclineKey.TAIL,
            key = keyOf(readable),
            substitution = PrefixProbe.Substitution(find = "Whenever ~ frobnicates,", replace = "When ~ enters,"),
        )

        result.cardsConsidered shouldBe 2
        result.familyLines shouldBe 2
        result.familyLinesParsing shouldBe 2
        result.cardsFinished shouldBe 1
    }

    "a payload the grammar still cannot read is counted, not assumed away" {
        val card = card("Unreadable Payload", "Whenever ~ frobnicates, blorp target creature.")

        val result = PrefixProbe.run(
            touchstone = touchstone,
            cards = listOf(card),
            family = DeclineKey.TAIL,
            key = keyOf(card),
            substitution = PrefixProbe.Substitution(find = "Whenever ~ frobnicates,", replace = "When ~ enters,"),
        )

        result.familyLinesParsing shouldBe 0
        result.cardsFinished shouldBe 0
    }

    "a span that does not occur is its own outcome — the pattern is describing another family" {
        val card = card("Elsewhere", "Whenever ~ frobnicates, draw a card.")

        val result = PrefixProbe.run(
            touchstone = touchstone,
            cards = listOf(card),
            family = DeclineKey.TAIL,
            key = keyOf(card),
            substitution = PrefixProbe.Substitution(find = "At the beginning of your upkeep,", replace = "When ~ enters,"),
        )

        result.unmatched shouldBe 1
        result.familyLinesParsing shouldBe 0
    }

    "a substitution that consumes the whole line reads it — the family's span IS the line" {
        // The modal header is the worked example: `Choose one —` is nothing but the missing
        // construct, so a band that reads modal spells reads it by the same assumption that lets
        // `When ~ enters,` stand in for any other span.
        val card = card("Whole Line", "Frobnicate —\nFlying")

        val result = PrefixProbe.run(
            touchstone = touchstone,
            cards = listOf(card),
            family = DeclineKey.TAIL,
            key = keyOf(card),
            substitution = PrefixProbe.Substitution(find = "Frobnicate —", replace = ""),
        )

        result.familyLinesParsing shouldBe 1
        result.cardsFinished shouldBe 1
    }

    "regex is opt-in, so Oracle punctuation typed as a literal cannot silently mis-measure" {
        val card = card("Symbols", "{T}: Frobnicate target creature.")
        val key = keyOf(card)

        // `{T}` as a literal is a literal; as a regex it would be a repetition operator.
        PrefixProbe.run(
            touchstone, listOf(card), DeclineKey.TAIL, key,
            PrefixProbe.Substitution(find = "{T}: Frobnicate", replace = "{T}: Destroy"),
        ).unmatched shouldBe 0

        PrefixProbe.run(
            touchstone, listOf(card), DeclineKey.TAIL, key,
            PrefixProbe.Substitution(find = "Frobnicate [", replace = "Destroy", regex = true),
        ).error!! shouldContain "not a regex"
    }
})
