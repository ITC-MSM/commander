package com.wingedsheep.assay.gate

import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleFace
import com.wingedsheep.assay.grammar.Grammar
import com.wingedsheep.assay.normalize.NormalizedFace
import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.normalize.Reminders
import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.deadToken
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.assay.grammar.CardFragment

/**
 * Gate 1 — the textual round trip, over every unique Oracle text in the Scryfall bulk.
 *
 * ```
 * print(parse(normalize(t))) == normalize(t)      // or: declined, and counted
 * ```
 *
 * Roughly thirty thousand assertions that need no human to read them. A rule that quietly drops
 * the word "other", or collapses "up to three" into "three", cannot survive it.
 *
 * Two refinements the bare formula leaves out, both of which the design implies and neither of
 * which weakens it:
 *
 * - **Normalization is gated first.** Before any grammar runs, `restore(lines) == raw` must hold
 *   for the face. A normalization that could not reproduce its own input would let *any* grammar
 *   look correct, so a failure here is counted separately and is always a bug in Assay.
 * - **Alternate spellings are a verdict, not a failure.** `canonical = false` rules exist so one
 *   meaning has one printed form; a card printed "Ward—{2}" therefore prints back as "Ward {2}".
 *   Nothing was lost — reparsing the printed line yields the identical model — so it is recorded
 *   as [LineVerdict.VARIANT] rather than counted as a round trip *or* as a mismatch. Only a
 *   printed line whose model does **not** survive reparsing is a [LineVerdict.MISMATCH], and that
 *   number must be zero.
 */
class Touchstone(private val grammar: Phrase<CardFragment> = Grammar.abilityLine) {

    fun assay(card: OracleCard): CardResult {
        val faces = card.faces.map { assayFace(card, it) }
        return CardResult(card = card, faces = faces, inPhase1Scope = Phase1Scope.isKeywordOnly(card, faces))
    }

    private fun assayFace(card: OracleCard, face: OracleFace): FaceResult {
        val normalized = Normalizer.normalize(face)
        val normalizationHolds = normalized.restore(normalized.lines) == face.oracleText

        val lines = normalized.lines.mapIndexed { index, line -> assayLine(card, face, index, line) }
        // The full inverse — printed lines back to the face's original bytes — is only meaningful
        // where every line already printed byte-exact; anywhere else it would fail for a reason the
        // line verdicts already named. Null means "not applicable", and false means a bug in
        // normalization's inverse that the per-face self-check above did not catch.
        val restoreHolds = if (!normalizationHolds || lines.any { it.verdict != LineVerdict.ROUND_TRIP }) {
            null
        } else {
            normalized.restore(lines.map { it.printed ?: it.line }) == face.oracleText
        }

        return FaceResult(
            cardName = card.name,
            faceName = face.name,
            normalized = normalized,
            normalizationHolds = normalizationHolds,
            restoreHolds = restoreHolds,
            lines = lines,
            glosses = auditGlosses(face),
        )
    }

    private fun assayLine(card: OracleCard, face: OracleFace, index: Int, line: String): LineResult {
        val outcome = grammar.parseLine(line)
        val redundant = (outcome as? ParseOutcome.Accepted)?.redundantReadings ?: 0

        fun result(
            verdict: LineVerdict,
            model: CardFragment? = null,
            printed: String? = null,
            decline: ParseOutcome.Declined? = null,
        ) = LineResult(card.name, face.name, index, line, verdict, model, printed, decline, redundant)

        return when (outcome) {
            is ParseOutcome.Declined -> result(LineVerdict.DECLINED, decline = outcome)
            is ParseOutcome.Ambiguous -> result(LineVerdict.AMBIGUOUS, model = outcome.readings.first())
            is ParseOutcome.Accepted -> {
                val printed = grammar.printLine(outcome.value)
                    ?: return result(LineVerdict.MISMATCH, model = outcome.value)
                when {
                    printed == line -> result(LineVerdict.ROUND_TRIP, outcome.value, printed)
                    // The printed spelling differs. It is a normalized *variant* only if the model
                    // survives the trip; otherwise the grammar lost or changed something.
                    (grammar.parseLine(printed) as? ParseOutcome.Accepted)?.value == outcome.value ->
                        result(LineVerdict.VARIANT, outcome.value, printed)

                    else -> result(LineVerdict.MISMATCH, outcome.value, printed)
                }
            }
        }
    }

    /**
     * The reminder-text audit: for each printed line that is a keyword plus a parenthetical gloss,
     * does regenerating the gloss from our model reproduce what Wizards printed?
     *
     * Reported, never gated. A `DIFFERED` row means our keyword model and the printed gloss
     * disagree about what the keyword does, which is worth reading; an `UNGLOSSED` row only means
     * [Reminders] has no entry yet.
     */
    private fun auditGlosses(face: OracleFace): List<GlossResult> {
        if (face.oracleText.isBlank()) return emptyList()
        val self = Reminders.selfNoun(face.typeLine)
        return face.oracleText.split("\n").mapNotNull { raw ->
            val m = GLOSSED_LINE.matchEntire(raw.trim()) ?: return@mapNotNull null
            val head = m.groupValues[1].trim()
            val printedGloss = m.groupValues[2]
            val ability = (grammar.parseLine(head) as? ParseOutcome.Accepted)
                ?.value?.keywordAbilities?.singleOrNull()
                ?: return@mapNotNull null
            val expected = Reminders.gloss(ability, self)
            GlossResult(
                cardName = face.name,
                keyword = head,
                verdict = when {
                    expected == null -> GlossVerdict.UNGLOSSED
                    expected == printedGloss -> GlossVerdict.MATCHED
                    else -> GlossVerdict.DIFFERED
                },
                printed = printedGloss,
                regenerated = expected,
            )
        }
    }

    private companion object {
        val GLOSSED_LINE = Regex("""([^(]+)\((.*)\)""")
    }
}

/** Which cards the Phase 1 acceptance number is measured over. */
object Phase1Scope {

    /**
     * Vanilla, or keyword-only: every printed line is a comma-joined run of the card's own
     * keywords.
     *
     * The classifier reads **Scryfall's** keyword tagging, deliberately not the grammar's rule
     * list. Scoping the acceptance number by what Assay already covers would make it measure
     * nothing; the point of the number is what fraction of an *independently defined* class the
     * grammar handles.
     */
    fun isKeywordOnly(card: OracleCard, faces: List<FaceResult>): Boolean {
        if (card.isVanilla) return true
        if (card.scryfallKeywords.isEmpty()) return false
        val heads = card.scryfallKeywords.map { it.lowercase() }
        return faces.all { face ->
            face.normalized.lines.all { line ->
                // Both separators: older cards print "Flying; banding" and they are as
                // keyword-only as "Flying, vigilance" is.
                line.isBlank() || line.split(", ", "; ").all { isKeywordShaped(it.trim(), heads) }
            }
        }
    }

    /**
     * Scryfall's `keywords` array also lists **ability words** — Domain, Delirium, Threshold — which
     * are typographic prefixes on ordinary rules text, not keyword abilities. Two shape rules
     * separate them, and both are about punctuation because that is exactly where the printed
     * difference lives:
     *
     * - A **spaced** em dash ("Domain — Enchanted creature gets…") is an ability word. A keyword
     *   ability's dash is unspaced ("Ward—Pay 2 life", "Suspend 4—{1}{R}").
     * - A segment that ends in a period *and* has no dash at all is a sentence, not a keyword.
     *
     * Conservative by design: it will exclude an odd genuine keyword (Prototype prints
     * "Prototype {1}{B} — 1/1"), which understates the denominator rather than flattering the
     * numerator.
     */
    private fun isKeywordShaped(segment: String, heads: List<String>): Boolean {
        if (segment.contains(" — ")) return false
        if (segment.endsWith(".") && !segment.contains("—")) return false
        val lower = segment.lowercase()
        return heads.any { lower == it || lower.startsWith("$it ") || lower.startsWith("$it—") }
    }
}

enum class LineVerdict {
    /** Printed back byte-for-byte. */
    ROUND_TRIP,

    /** An alternate spelling normalized to its canonical form; the model survived unchanged. */
    VARIANT,

    /** Parsed, then printed something the grammar does not read back the same way. Must be 0. */
    MISMATCH,

    /** Two rules gave two different models for one text. Must be 0. */
    AMBIGUOUS,

    /** The grammar does not cover this text. Counted and ranked, never approximated. */
    DECLINED,
}

data class LineResult(
    val cardName: String,
    val faceName: String,
    val index: Int,
    val line: String,
    val verdict: LineVerdict,
    val model: CardFragment?,
    val printed: String?,
    val decline: ParseOutcome.Declined?,
    /** Extra readings that produced the *same* model — grammar redundancy, reported not gated. */
    val redundantReadings: Int = 0,
) {
    /** The token a decline died on — the key the fineness report ranks declines by. */
    val declineToken: String? get() = decline?.deadToken(line)
}

data class FaceResult(
    val cardName: String,
    val faceName: String,
    val normalized: NormalizedFace,
    /** `restore(normalized.lines) == raw` — normalization reproducing its own input. */
    val normalizationHolds: Boolean,
    /** `restore(printedLines) == raw` — the full inverse. Null when the line verdicts make it moot. */
    val restoreHolds: Boolean?,
    val lines: List<LineResult>,
    val glosses: List<GlossResult>,
)

data class CardResult(
    val card: OracleCard,
    val faces: List<FaceResult>,
    val inPhase1Scope: Boolean,
) {
    val lines: List<LineResult> get() = faces.flatMap { it.lines }

    /** A card round-trips when every line of every face does, spelling included. */
    val roundTrips: Boolean get() = lines.all { it.verdict == LineVerdict.ROUND_TRIP }

    /** …and nothing was lost, allowing for alternate spellings normalized to canonical form. */
    val covered: Boolean
        get() = lines.all { it.verdict == LineVerdict.ROUND_TRIP || it.verdict == LineVerdict.VARIANT }
}

enum class GlossVerdict { MATCHED, DIFFERED, UNGLOSSED }

data class GlossResult(
    val cardName: String,
    val keyword: String,
    val verdict: GlossVerdict,
    val printed: String,
    val regenerated: String?,
)
