package com.wingedsheep.assay.gate

import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine

/**
 * The **feasibility probe** — the second half of the ranking, and the half that makes the first one
 * honest.
 *
 * A family's sole-blocked count says which cards a band *reaches*. It does not say which lines the
 * band *finishes*, and those are different numbers because a declined line is a prefix the grammar
 * could not read followed by a payload it may or may not already read. Every ranking this module has
 * used that skipped the difference has overstated its band, four times in the same direction — the
 * spell-cast family predicted 234 whole cards and delivered 183.
 *
 * The measurement that closes it is a substitution: put a **known-good prefix** where the family's
 * own span is, re-parse, and count what got through. If "Whenever you cast a noncreature spell,
 * draw a card." becomes "When ~ enters, draw a card." and that parses, then the only thing between
 * that card and coverage is the cast-trigger prefix. If it still declines, the payload is missing
 * too and writing the prefix buys nothing on that card.
 *
 * ## What it is and is not
 *
 * It is a **prediction**, and it assumes the construct you would write reads everything the family's
 * own span does. Substituting `When ~ enters,` for `Whenever you cast a noncreature spell,` tests
 * whether the *rest* of each line is readable; it cannot tell you that a cast trigger is expressible
 * in `mtg-sdk` at all. A family whose span turns out to be an SDK gap will measure exactly as well
 * as one whose span is an afternoon of grammar, which is why this is one number beside the others
 * rather than the ranking itself.
 *
 * It is also not precomputable — the substitution is chosen per family by whoever is looking at it —
 * which is exactly why it belongs on a page that calls the live grammar rather than in a report.
 * The computation lives here in `gate/` all the same, so the explorer is calling a measurement
 * rather than being one.
 */
object PrefixProbe {

    /**
     * @param find the family's own span. [regex] false means a literal substring, which is the
     *   default because Oracle text is dense with `{`, `(`, `.` and `+` and a literal typed into a
     *   regex box is a silent mis-measurement rather than an error. Regex is there because the
     *   families worth the most vary in the middle — `Whenever you cast [^,]*,` is one span and
     *   twelve hundred spellings of it.
     * @param replace the known-good prefix, always literal.
     */
    data class Substitution(val find: String, val replace: String, val regex: Boolean = false)

    /**
     * @param familyLines the family's own declined lines, over every card behind it.
     * @param familyLinesParsing how many of those parse after the substitution — the fraction of the
     *   family the rest of the grammar can already finish.
     * @param unmatched lines the [Substitution.find] span did not occur in at all. Reported rather
     *   than folded into the failures, because a large number here means the span is wrong and the
     *   whole measurement is about a different family than the one on screen.
     * @param cardsFinished cards **all** of whose declined lines parse after the substitution. This
     *   is the number the card counts hide and the one a band is actually worth.
     * @param examples a handful of before/after pairs, so the substitution can be eyeballed rather
     *   than trusted.
     */
    data class Result(
        val familyLines: Int,
        val familyLinesParsing: Int,
        val unmatched: Int,
        val cardsConsidered: Int,
        val cardsFinished: Int,
        val examples: List<Example>,
        val error: String? = null,
    )

    data class Example(val card: String, val before: String, val after: String, val parses: Boolean)

    /** How many before/after pairs a page needs to believe the substitution. */
    private const val MAX_EXAMPLES = 8

    fun failed(message: String) = Result(0, 0, 0, 0, 0, emptyList(), error = message)

    /**
     * Re-assays [cards] and applies [substitution] to every declined line of each.
     *
     * Every declined line is substituted, not only the family's own: "whole cards finished" is a
     * claim about a *card*, so a second declined line on it has to be accounted for. It normally
     * fails to match and therefore fails to parse, which is the correct answer — that card is
     * blocked by something else as well.
     */
    fun run(
        touchstone: Touchstone,
        cards: List<OracleCard>,
        family: DeclineKey,
        key: String,
        substitution: Substitution,
        tailWords: Int = DeclineKey.TAIL_WORDS,
    ): Result {
        if (substitution.find.isEmpty()) return failed("give the span to replace")
        val pattern = if (substitution.regex) {
            runCatching { Regex(substitution.find) }.getOrElse { return failed("not a regex: ${it.message}") }
        } else {
            null
        }

        var familyLines = 0
        var familyLinesParsing = 0
        var unmatched = 0
        var cardsConsidered = 0
        var cardsFinished = 0
        val examples = mutableListOf<Example>()

        for (card in cards) {
            val result = touchstone.assay(card)
            val declined = result.lines.filter { it.verdict == LineVerdict.DECLINED }
            // By position rather than by value: a card can print the same declined line twice, and
            // two equal LineResults would then both count as the one this family owns.
            val mine = declined.indices.filterTo(HashSet()) { family.of(declined[it], tailWords) == key }
            if (mine.isEmpty()) continue
            cardsConsidered++

            var wholeCard = result.lines.none {
                it.verdict == LineVerdict.MISMATCH || it.verdict == LineVerdict.AMBIGUOUS
            }
            declined.forEachIndexed { index, line ->
                val substituted = substitute(line.line, pattern, substitution)
                val parses = substituted != null && reads(touchstone, substituted)
                if (!parses) wholeCard = false
                if (index in mine) {
                    familyLines++
                    if (substituted == null) unmatched++ else if (parses) familyLinesParsing++
                    if (examples.size < MAX_EXAMPLES) {
                        examples.add(Example(card.name, line.line, substituted ?: "", parses))
                    }
                }
            }
            if (wholeCard) cardsFinished++
        }

        return Result(
            familyLines = familyLines,
            familyLinesParsing = familyLinesParsing,
            unmatched = unmatched,
            cardsConsidered = cardsConsidered,
            cardsFinished = cardsFinished,
            examples = examples,
        )
    }

    /**
     * A substituted line reads if the grammar gets through it — **or if the substitution consumed
     * all of it**.
     *
     * The empty case is not a loophole, it is the modal header. `Choose one —` is a whole line that
     * is nothing but the missing construct, and a band that reads modal spells reads it by the same
     * assumption under which `When ~ enters,` stands in for `Whenever you cast a noncreature spell,`
     * — the construct exists, so its own span is read. Refusing it would report zero finished cards
     * for every family whose span is the entire line, which is precisely the family shape the tail
     * ranking exists to surface. It does mean `find = ".*"` measures nothing but itself; this is an
     * exploratory box whose output is labelled a prediction, not a gate.
     */
    private fun reads(touchstone: Touchstone, line: String): Boolean =
        line.isBlank() || touchstone.grammar.parseLine(line) !is ParseOutcome.Declined

    /** Null when the span does not occur — a distinct outcome from "substituted and still declines". */
    private fun substitute(line: String, pattern: Regex?, substitution: Substitution): String? =
        if (pattern != null) {
            pattern.find(line)?.let { line.replaceRange(it.range, substitution.replace) }
        } else {
            val at = line.indexOf(substitution.find)
            if (at < 0) null else line.replaceRange(at, at + substitution.find.length, substitution.replace)
        }

    /**
     * A first guess at the family's span, for the box to open with.
     *
     * Under [DeclineKey.TAIL] the key is the tail's first few words, so the span the user wants is
     * the example line's own prefix *plus* that tail — the grammar died at the start of the tail, so
     * everything before it already reads and everything the family owns starts there. Under
     * [DeclineKey.SHAPE] the whole line is the key and its opening words are the closest thing to a
     * span. Either way it is a starting point to extend, not an answer: the span usually runs a few
     * words past where the key stops, and only the person reading the examples knows how far.
     */
    fun suggestedSpan(family: DeclineKey, example: String, key: String): String = when (family) {
        DeclineKey.TAIL -> {
            val tail = key.removeSuffix(" …")
            // The skeleton collapsed numbers and symbols, so the key is not literally in the line;
            // fall back to the tail itself, which the user will edit anyway.
            val at = example.indexOf(tail)
            if (at >= 0) example.take(at + tail.length) else tail
        }

        DeclineKey.SHAPE -> example.split(" ").take(4).joinToString(" ")
        DeclineKey.TOKEN -> key
    }
}
