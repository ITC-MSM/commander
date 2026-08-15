package com.wingedsheep.assay.gate

import java.util.Locale

/**
 * Fineness — the coverage metric, in parts per thousand.
 *
 * An assay never reports "looks fine"; it reports purity as a number. This is that number, plus
 * the two things that make it actionable: what the grammar refused, and what it would unlock if it
 * stopped refusing.
 *
 * The builder consumes results one card at a time and keeps only counters and bounded examples, so
 * a whole-corpus run costs a fixed amount of memory rather than 38k retained parse trees.
 */
class FinenessReport private constructor(
    val cards: Int,
    val faces: Int,
    val vanillaFaces: Int,
    val lineInstances: Int,
    val uniqueLines: Int,
    val instancesByVerdict: Map<LineVerdict, Int>,
    val uniqueByVerdict: Map<LineVerdict, Int>,
    val cardsCovered: Int,
    val cardsRoundTripped: Int,
    val inScopeCards: Int,
    val inScopeCovered: Int,
    val normalizationFailures: List<String>,
    val restoreFailures: List<String>,
    val ambiguities: List<String>,
    val mismatches: List<String>,
    val declines: List<Decline>,
    val glossCounts: Map<GlossVerdict, Int>,
    val glossDifferences: List<GlossResult>,
    val redundantReadingLines: Int,
) {

    /** One decline family: the token the grammar died on, and how much is behind it. */
    data class Decline(val token: String, val cards: Int, val lines: Int, val example: String)

    val fineness: Double get() = permil(instancesByVerdict[LineVerdict.ROUND_TRIP] ?: 0, lineInstances)
    val uniqueFineness: Double get() = permil(uniqueByVerdict[LineVerdict.ROUND_TRIP] ?: 0, uniqueLines)
    val inScopeFineness: Double get() = permil(inScopeCovered, inScopeCards)

    /** The gate is red on anything that is a bug rather than a gap. Declines are not bugs. */
    val clean: Boolean
        get() = normalizationFailures.isEmpty() && restoreFailures.isEmpty() &&
            ambiguities.isEmpty() && mismatches.isEmpty()

    /**
     * @param population names the subset the numbers were measured over, when it is not the whole
     *   corpus. A fineness number without its denominator's *definition* beside it is the easiest
     *   way for two runs to be compared that never measured the same thing.
     */
    fun render(topDeclines: Int = 20, population: String? = null): String = buildString {
        appendLine("Argentum Assay — fineness")
        appendLine("=".repeat(78))
        appendLine()
        if (population != null) {
            appendLine(row("Population", population))
            appendLine()
        }
        appendLine(row("Cards assayed", cards.toString()))
        appendLine(row("Faces", "$faces  ($vanillaFaces vanilla)"))
        appendLine(row("Ability lines", "$lineInstances  ($uniqueLines unique)"))
        appendLine()
        appendLine(row("Round-trips byte-exact", "${count(LineVerdict.ROUND_TRIP)}   ${permilText(fineness)}"))
        appendLine(row("  …of unique lines", "${uniqueCount(LineVerdict.ROUND_TRIP)}   ${permilText(uniqueFineness)}"))
        appendLine(row("Alternate spelling normalized", count(LineVerdict.VARIANT).toString()))
        appendLine(row("Declined", count(LineVerdict.DECLINED).toString()))
        appendLine(row("Ambiguous — distinct readings", "${count(LineVerdict.AMBIGUOUS)}   ${verdictNote(ambiguities)}"))
        appendLine(row("Print mismatch", "${count(LineVerdict.MISMATCH)}   ${verdictNote(mismatches)}"))
        appendLine(row("Normalization not invertible", "${normalizationFailures.size}   ${verdictNote(normalizationFailures)}"))
        appendLine(row("Full inverse not reproduced", "${restoreFailures.size}   ${verdictNote(restoreFailures)}"))
        appendLine(row("Redundant readings (same model)", redundantReadingLines.toString()))
        appendLine()
        appendLine(row("Cards fully covered", "$cardsCovered / $cards   ${permilText(permil(cardsCovered, cards))}"))
        appendLine(row("  …byte-exact", cardsRoundTripped.toString()))
        appendLine(
            row(
                "Vanilla + keyword-only cards",
                "$inScopeCovered / $inScopeCards   ${permilText(inScopeFineness)}   <- Phase 1 target",
            )
        )
        appendLine()
        appendLine(
            row(
                "Reminder-text glosses",
                "${gloss(GlossVerdict.MATCHED)} matched · ${gloss(GlossVerdict.DIFFERED)} differed · " +
                    "${gloss(GlossVerdict.UNGLOSSED)} unglossed",
            )
        )

        if (mismatches.isNotEmpty()) {
            appendLine()
            appendLine("PRINT MISMATCHES (must be 0)")
            appendLine("-".repeat(78))
            mismatches.forEach { appendLine("  $it") }
        }
        if (ambiguities.isNotEmpty()) {
            appendLine()
            appendLine("AMBIGUITIES (must be 0)")
            appendLine("-".repeat(78))
            ambiguities.forEach { appendLine("  $it") }
        }
        if (normalizationFailures.isNotEmpty()) {
            appendLine()
            appendLine("NORMALIZATION NOT INVERTIBLE (must be 0)")
            appendLine("-".repeat(78))
            normalizationFailures.forEach { appendLine("  $it") }
        }
        if (restoreFailures.isNotEmpty()) {
            appendLine()
            appendLine("FULL INVERSE NOT REPRODUCED (must be 0)")
            appendLine("-".repeat(78))
            restoreFailures.forEach { appendLine("  $it") }
        }
        if (glossDifferences.isNotEmpty()) {
            appendLine()
            appendLine("REMINDER-TEXT DISAGREEMENTS (findings, not failures)")
            appendLine("-".repeat(78))
            glossDifferences.forEach {
                appendLine("  ${it.cardName} — ${it.keyword}")
                appendLine("    printed:     ${it.printed}")
                appendLine("    regenerated: ${it.regenerated}")
            }
        }

        appendLine()
        appendLine("TOP DECLINES, ranked by cards blocked")
        appendLine("-".repeat(78))
        if (declines.isEmpty()) {
            appendLine("  (none)")
        } else {
            appendLine("  %-6s %-6s %-22s %s".format(Locale.ROOT, "cards", "lines", "died on", "example"))
            declines.take(topDeclines).forEach {
                appendLine(
                    "  %-6d %-6d %-22s %s".format(
                        Locale.ROOT,
                        it.cards,
                        it.lines,
                        it.token.take(22),
                        it.example.take(44),
                    )
                )
            }
            if (declines.size > topDeclines) {
                appendLine("  … and ${declines.size - topDeclines} more decline families")
            }
        }
    }

    private fun count(v: LineVerdict) = instancesByVerdict[v] ?: 0
    private fun uniqueCount(v: LineVerdict) = uniqueByVerdict[v] ?: 0
    private fun gloss(v: GlossVerdict) = glossCounts[v] ?: 0
    private fun verdictNote(items: List<*>) = if (items.isEmpty()) "" else "<- READ THESE"

    private fun row(label: String, value: String) = "  %-32s %s".format(Locale.ROOT, label, value).trimEnd()

    /**
     * Fineness is parts per thousand — the assay metric the design names — which reads as a
     * percentage at a glance and is off by a factor of ten when it does. The percent is printed
     * beside it rather than instead of it, so neither reading can be the wrong one.
     *
     * Locale.ROOT throughout: a report that prints "187,9" on one machine and "187.9" on another
     * is not diffable, and this one is meant to be pasted into PRs.
     */
    private fun permilText(value: Double) = "%.1f‰ (%.1f%%)".format(Locale.ROOT, value, value / 10.0)

    companion object {
        fun permil(part: Int, whole: Int): Double = if (whole == 0) 0.0 else part * 1000.0 / whole

        fun builder() = Builder()
    }

    class Builder {
        private var cards = 0
        private var faces = 0
        private var vanillaFaces = 0
        private var lineInstances = 0
        private var cardsCovered = 0
        private var cardsRoundTripped = 0
        private var inScopeCards = 0
        private var inScopeCovered = 0
        private var redundantReadingLines = 0

        private val instancesByVerdict = mutableMapOf<LineVerdict, Int>()
        private val seenLines = HashMap<String, LineVerdict>()
        private val normalizationFailures = mutableListOf<String>()
        private val restoreFailures = mutableListOf<String>()
        private val ambiguities = mutableListOf<String>()
        private val mismatches = mutableListOf<String>()
        private val glossCounts = mutableMapOf<GlossVerdict, Int>()
        private val glossDifferences = mutableListOf<GlossResult>()

        private val declineLines = mutableMapOf<String, Int>()
        private val declineCards = mutableMapOf<String, MutableSet<String>>()
        private val declineExample = mutableMapOf<String, String>()

        fun add(result: CardResult) = apply {
            cards++
            if (result.covered) cardsCovered++
            if (result.roundTrips) cardsRoundTripped++
            if (result.inPhase1Scope) {
                inScopeCards++
                if (result.covered) inScopeCovered++
            }

            for (face in result.faces) {
                faces++
                if (face.normalized.isVanilla) vanillaFaces++
                if (!face.normalizationHolds) {
                    record(normalizationFailures, "${face.cardName} / ${face.faceName}")
                }
                if (face.restoreHolds == false) {
                    record(restoreFailures, "${face.cardName} / ${face.faceName}")
                }
                for (gloss in face.glosses) {
                    glossCounts.merge(gloss.verdict, 1, Int::plus)
                    if (gloss.verdict == GlossVerdict.DIFFERED) record(glossDifferences, gloss)
                }
                for (line in face.lines) {
                    lineInstances++
                    instancesByVerdict.merge(line.verdict, 1, Int::plus)
                    if (line.redundantReadings > 0) redundantReadingLines++
                    // First verdict wins for a repeated line: identical text parses identically,
                    // so this only picks which card's row the example comes from.
                    seenLines.putIfAbsent(line.line, line.verdict)
                    when (line.verdict) {
                        LineVerdict.AMBIGUOUS -> record(ambiguities, "${result.card.name}: \"${line.line}\"")
                        LineVerdict.MISMATCH ->
                            record(mismatches, "${result.card.name}: \"${line.line}\" -> \"${line.printed}\"")

                        LineVerdict.DECLINED -> {
                            val token = line.declineToken ?: "<unknown>"
                            declineLines.merge(token, 1, Int::plus)
                            declineCards.getOrPut(token) { mutableSetOf() }.add(result.card.name)
                            declineExample.putIfAbsent(token, line.line)
                        }

                        else -> Unit
                    }
                }
            }
        }

        fun build(): FinenessReport {
            val uniqueByVerdict = seenLines.values.groupingBy { it }.eachCount()
            val declines = declineLines.map { (token, lines) ->
                Decline(
                    token = token,
                    cards = declineCards[token]?.size ?: 0,
                    lines = lines,
                    example = declineExample[token].orEmpty(),
                )
            }.sortedWith(compareByDescending<Decline> { it.cards }.thenByDescending { it.lines })

            return FinenessReport(
                cards = cards,
                faces = faces,
                vanillaFaces = vanillaFaces,
                lineInstances = lineInstances,
                uniqueLines = seenLines.size,
                instancesByVerdict = instancesByVerdict.toMap(),
                uniqueByVerdict = uniqueByVerdict,
                cardsCovered = cardsCovered,
                cardsRoundTripped = cardsRoundTripped,
                inScopeCards = inScopeCards,
                inScopeCovered = inScopeCovered,
                normalizationFailures = normalizationFailures.toList(),
                restoreFailures = restoreFailures.toList(),
                ambiguities = ambiguities.toList(),
                mismatches = mismatches.toList(),
                declines = declines,
                glossCounts = glossCounts.toMap(),
                glossDifferences = glossDifferences.toList(),
                redundantReadingLines = redundantReadingLines,
            )
        }

        /** Examples are bounded: a corpus-wide failure should not print 38k identical rows. */
        private fun <T> record(into: MutableList<T>, item: T) {
            if (into.size < MAX_EXAMPLES) into.add(item)
        }

        private companion object {
            const val MAX_EXAMPLES = 40
        }
    }
}
