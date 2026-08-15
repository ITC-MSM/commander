package com.wingedsheep.assay.syntax

/**
 * The one case rule Oracle-ese needs, applied at the text boundary rather than inside the grammar.
 *
 * An ability line is sentence-cased: `"Flying, first strike"` — the same keyword is capitalized
 * first and lowercase third. Templates are therefore written in their **mid-sentence** form
 * (`"flying"`, `"first strike"`, `"ward {2}"`), and this pass decapitalizes a line before parsing
 * and recapitalizes after printing.
 *
 * It is *not* a normalization pass in the [com.wingedsheep.assay.normalize] sense, and deliberately
 * so: it moves no information. It only lowercases a leading letter that Oracle templating
 * guarantees is uppercase, and refuses (rather than silently repairing) a line that starts with a
 * lowercase letter, since that would make the inverse a guess.
 *
 * Lines that start with a symbol or digit — `"{T}: Add {C}."`, `"1 or more"` — pass through
 * untouched in both directions, which is why the guard is on *lowercase* specifically rather than
 * on "not uppercase".
 *
 * ## A line has more than one sentence start
 *
 * `"{T}: Add {C}."` capitalizes "Add", and `"{2}, {T}: Draw a card."` capitalizes "Draw", because
 * an activated ability's effect clause begins a sentence after the cost colon. A full stop starts
 * one for the same reason: "Target creature gets +1/+3 until end of turn. Untap that creature." is
 * two sentences on one printed line. That is the same templating rule the line start obeys, applied
 * at every place Oracle applies it, so it belongs here rather than in a grammar combinator — the
 * alternative is every activated-ability and every second-clause rule spelling its verbs
 * capitalized, which is exactly the re-spelling that would stop
 * [com.wingedsheep.assay.grammar.Steps] being slottable into a new sentence context.
 *
 * The rule is Wizards' and the corpus states it: of 14,042 `": "` occurrences in Oracle text, 32
 * are followed by a lowercase letter, and all 32 are prose enumerations on the "hero's journey"
 * cards ("• Setting: a land") rather than ability costs. Of every `". "` in the corpus, 15 are
 * followed by a lowercase letter and every one is an Un-set joke card or an abbreviation
 * ("B.F.M.", "S.N.E.A.K.", "Ph.D."). Those lines decline, which is what [decapitalize] returning
 * null means, and they declined before this too.
 */
object SentenceCase {

    /**
     * Where Oracle starts a sentence inside one ability line: the line itself, each clause after an
     * ability cost's `": "`, and each sentence after a full stop.
     *
     * Positions rather than a rewrite, because both directions need the same list and a
     * one-character-for-one-character substitution keeps every index stable between them.
     */
    private fun sentenceStarts(line: String): List<Int> =
        (listOf(0) + SENTENCE_BREAK.findAll(line).map { it.range.last + 1 }).filter { it < line.length }

    /** Line as the grammar sees it, or null when a leading character makes the inverse a guess. */
    fun decapitalize(line: String): String? {
        val chars = line.toCharArray()
        for (at in sentenceStarts(line)) {
            val c = chars[at]
            if (c.isLowerCase()) return null
            if (c.isUpperCase()) chars[at] = c.lowercaseChar()
        }
        return String(chars)
    }

    /** Inverse of [decapitalize]: the printed line as Oracle templating spells it. */
    fun capitalize(line: String): String {
        val chars = line.toCharArray()
        for (at in sentenceStarts(line)) {
            val c = chars[at]
            if (c.isLowerCase()) chars[at] = c.uppercaseChar()
        }
        return String(chars)
    }

    /**
     * The two places Oracle starts a new sentence *inside* one ability line: after an ability
     * cost's `": "`, and after a full stop.
     *
     * The full stop is what lets a line spelling two sentences — "Target creature gets +1/+3 until
     * end of turn. Untap that creature." — slot the ordinary effect vocabulary twice instead of
     * needing a capitalized copy of every verb. It is the same argument the cost colon carries, and
     * it is why this file exists rather than a `capitalized(...)` combinator in the grammar.
     */
    private val SENTENCE_BREAK = Regex("""(?:: |\. )""")
}

/** Parse a whole sentence-cased ability line. */
fun <T> Phrase<T>.parseLine(line: String, parseCap: Int = ParseContext.DEFAULT_PARSE_CAP): ParseOutcome<T> {
    val body = SentenceCase.decapitalize(line)
        ?: return ParseOutcome.Declined(0, listOf("a capitalized first word"), DeclineReason.NO_PARSE)
    return parseText(body, parseCap)
}

/** Print a whole sentence-cased ability line. */
fun <T> Phrase<T>.printLine(value: T): String? = unparse(value)?.let(SentenceCase::capitalize)
