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
    val declineFamilies: Map<DeclineKey, List<Decline>>,
    val glossCounts: Map<GlossVerdict, Int>,
    val glossDifferences: List<GlossResult>,
    val redundantReadingLines: Int,
) {

    /**
     * One decline family, under one [DeclineKey].
     *
     * **Cards blocked is not cards unlocked**, and the gap between them is the most useful pair of
     * numbers here. A card is covered only when *every* one of its lines parses, so [cards] says how
     * many cards a family is *mentioned by* — not how many would come into coverage if it were
     * written. The module's own worked example: 410 cards decline on "At the beginning of…", and
     * adding every step-trigger prefix moved whole-card coverage by 23, because the other 387 were
     * blocked on their effect clause all along. A list showing only the 410 sends you at the wrong
     * work.
     *
     * @param soleBlocked cards this family is the *only* thing blocking — the honest count. Null
     *   under [DeclineKey.TOKEN], where the number is well-defined and means nothing; see
     *   [DeclineKey.namesWork].
     */
    data class Decline(
        val key: String,
        val cards: Int,
        val lines: Int,
        val soleBlocked: Int?,
        val example: String,
    )

    /** The families under one keying, ranked by cards blocked. */
    fun declines(key: DeclineKey): List<Decline> = declineFamilies[key].orEmpty()

    /** The token ranking, which is what "the decline list" has always meant without qualification. */
    val declines: List<Decline> get() = declines(DeclineKey.TOKEN)

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
     * @param ranking which keying the decline table is grouped by. [DeclineKey.TOKEN] is the
     *   default because it is what this report has always printed and what the gate's own diagnostic
     *   value rests on; [DeclineKey.TAIL] is the one to pass when the question is what to write
     *   next.
     */
    fun render(
        topDeclines: Int = 20,
        population: String? = null,
        ranking: DeclineKey = DeclineKey.TOKEN,
    ): String = buildString {
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
        appendLine(declineTable(topDeclines, ranking))
    }

    /**
     * The ranked gap report — the module's primary product, in whichever keying was asked for.
     *
     * `sole` is printed beside `cards` rather than instead of it, and only where it means something,
     * because the pair *is* the finding: a family with 819 cards and 126 sole-blocked is a different
     * piece of work from one with 504 of each, and a table showing either number alone hides which
     * one you are looking at.
     */
    private fun declineTable(top: Int, ranking: DeclineKey): String = buildString {
        val families = declines(ranking)
        val sole = ranking.namesWork
        appendLine("TOP DECLINES, keyed on ${keyingNote(ranking)}, ranked by cards blocked")
        appendLine("-".repeat(78))
        if (families.isEmpty()) {
            appendLine("  (none)")
            return@buildString
        }
        val header = if (sole) {
            "  %-6s %-6s %-6s %-26s %s".format(Locale.ROOT, "cards", "sole", "lines", ranking.name.lowercase(), "example")
        } else {
            "  %-6s %-6s %-26s %s".format(Locale.ROOT, "cards", "lines", ranking.name.lowercase(), "example")
        }
        appendLine(header)
        families.take(top).forEach {
            appendLine(
                if (sole) {
                    "  %-6d %-6d %-6d %-26s %s".format(
                        Locale.ROOT, it.cards, it.soleBlocked ?: 0, it.lines, it.key.take(26), it.example.take(40)
                    )
                } else {
                    "  %-6d %-6d %-26s %s".format(
                        Locale.ROOT, it.cards, it.lines, it.key.take(26), it.example.take(40)
                    )
                }
            )
        }
        if (families.size > top) appendLine("  … and ${families.size - top} more decline families")
        if (sole) {
            appendLine()
            appendLine("  `sole` is cards *all* of whose declined lines fall in the family — the number a")
            appendLine("  band actually delivers, against `cards`, which is the number it merely reaches.")
            appendLine("  It is still an upper bound: it says which cards the family blocks, not which")
            appendLine("  lines the rest of the grammar could finish. `just assay-explore` has the probe")
            appendLine("  that measures that, on the decline family's own page.")
        }
    }

    private fun keyingNote(ranking: DeclineKey) = when (ranking) {
        DeclineKey.TOKEN -> "the token each line died on"
        DeclineKey.SHAPE -> "the whole line, skeletonized"
        DeclineKey.TAIL -> "the parse's tail — the text from the decline onward"
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

        /**
         * @param tailWords how many words of [DeclineKey.TAIL] make a family. Threaded rather than
         *   read from the constant so the parameter stays re-measurable from the command line — see
         *   [DeclineKey.TAIL] for why it is a measurement and not a magic number.
         */
        fun builder(tailWords: Int = DeclineKey.TAIL_WORDS) = Builder(tailWords)
    }

    class Builder(private val tailWords: Int = DeclineKey.TAIL_WORDS) {
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

        /**
         * All three keyings, accumulated in the one pass.
         *
         * Every keying costs a hash lookup per declined line and a card-name set whose total size is
         * bounded by the number of declined line *instances*, so keeping three where there used to
         * be one does not change the builder's "counters and bounded examples" cost model — and
         * keeping them here rather than in the explorer is what stops the tail ranking being a
         * second implementation the gate cannot print.
         */
        private val families = DeclineKey.entries.associateWith { Families() }

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

                        else -> Unit
                    }
                }
            }

            // Keyed off the whole card rather than per face, because "sole-blocked" is a statement
            // about a *card*: a two-faced card whose back is read and whose front is not is blocked
            // by one family, and counting faces separately would say two.
            val declined = result.lines.filter { it.verdict == LineVerdict.DECLINED }
            if (declined.isNotEmpty()) {
                for ((dimension, family) in families) {
                    val keys = declined.map { dimension.of(it, tailWords) }
                    keys.forEachIndexed { i, key -> family.observe(key, result.card.name, declined[i].line) }
                    if (dimension.namesWork) {
                        keys.distinct().singleOrNull()?.let { family.soleBlocked.merge(it, 1, Int::plus) }
                    }
                }
            }
        }

        fun build(): FinenessReport {
            val uniqueByVerdict = seenLines.values.groupingBy { it }.eachCount()
            val declines = families.mapValues { (dimension, family) -> family.build(dimension) }

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
                declineFamilies = declines,
                glossCounts = glossCounts.toMap(),
                glossDifferences = glossDifferences.toList(),
                redundantReadingLines = redundantReadingLines,
            )
        }

        /** Examples are bounded: a corpus-wide failure should not print 38k identical rows. */
        private fun <T> record(into: MutableList<T>, item: T) {
            if (into.size < MAX_EXAMPLES) into.add(item)
        }

        /**
         * One keying's families, accumulated card by card.
         *
         * The card sets are **uncapped** on purpose: they are what the ranking is computed from, and
         * capping them made the top of the list a plateau of families that all reported exactly the
         * cap. It costs nothing — the total number of (family, card) pairs is bounded by the number
         * of declined lines.
         */
        private class Families {
            private val lines = LinkedHashMap<String, Int>()
            private val cards = HashMap<String, MutableSet<String>>()
            private val example = HashMap<String, String>()
            val soleBlocked = HashMap<String, Int>()

            fun observe(key: String, cardName: String, line: String) {
                lines.merge(key, 1, Int::plus)
                cards.getOrPut(key) { LinkedHashSet() }.add(cardName)
                example.putIfAbsent(key, line)
            }

            fun build(dimension: DeclineKey): List<Decline> =
                lines.map { (key, count) ->
                    Decline(
                        key = key,
                        cards = cards[key]?.size ?: 0,
                        lines = count,
                        soleBlocked = if (dimension.namesWork) soleBlocked[key] ?: 0 else null,
                        example = example[key].orEmpty(),
                    )
                }.sortedWith(compareByDescending<Decline> { it.cards }.thenByDescending { it.lines })
        }

        private companion object {
            const val MAX_EXAMPLES = 40
        }
    }
}
