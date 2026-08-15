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
 */
object SentenceCase {

    /** Line as the grammar sees it, or null when the leading character makes the inverse a guess. */
    fun decapitalize(line: String): String? {
        val first = line.firstOrNull() ?: return line
        if (first.isLowerCase()) return null
        if (!first.isUpperCase()) return line
        return first.lowercaseChar() + line.substring(1)
    }

    /** Inverse of [decapitalize]: the printed line as Oracle templating spells it. */
    fun capitalize(line: String): String {
        val first = line.firstOrNull() ?: return line
        if (!first.isLowerCase()) return line
        return first.uppercaseChar() + line.substring(1)
    }
}

/** Parse a whole sentence-cased ability line. */
fun <T> Phrase<T>.parseLine(line: String, parseCap: Int = ParseContext.DEFAULT_PARSE_CAP): ParseOutcome<T> {
    val body = SentenceCase.decapitalize(line)
        ?: return ParseOutcome.Declined(0, listOf("a capitalized first word"), DeclineReason.NO_PARSE)
    return parseText(body, parseCap)
}

/** Print a whole sentence-cased ability line. */
fun <T> Phrase<T>.printLine(value: T): String? = unparse(value)?.let(SentenceCase::capitalize)
