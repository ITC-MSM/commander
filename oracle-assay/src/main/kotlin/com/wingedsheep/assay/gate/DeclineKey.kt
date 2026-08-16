package com.wingedsheep.assay.gate

/**
 * How a declined line becomes a **family** — the three keyings the decline list can be ranked by.
 *
 * This lives in `gate/` rather than in the explorer because the explorer is a view and never a
 * second source of truth: `assay report --rank tail` and the explorer's tail page must be the same
 * ranking, and the cheapest guarantee of that is one key function that both call.
 *
 * The three answer three different questions, and they disagree in ways that change what gets
 * written next. That is not a defect of any of them; it is why there are three.
 *
 * | Key | Question | Bias |
 * |---|---|---|
 * | [TOKEN] | what is the grammar missing? | over-weights a missing **prefix** |
 * | [SHAPE] | which whole sentence needs a rule? | under-weights a missing **prefix or clause** |
 * | [TAIL]  | what construct would let these lines get further? | — |
 */
enum class DeclineKey {

    /**
     * The token the parse died on.
     *
     * The right key for "what is the grammar missing" and the wrong one for "what should I write".
     * A line dies at its *first* unknown token, so a trigger whose prefix is already read dies
     * somewhere after the comma while a trigger whose prefix is unknown dies on "At" — one missing
     * verb therefore lands in several buckets, and a missing prefix looks larger than it is.
     */
    TOKEN {
        override fun of(line: LineResult, tailWords: Int) = line.declineToken ?: NO_DECLINE
    },

    /**
     * The whole line, skeletonized — numbers to `#`, mana and tap symbols to `{§}`.
     *
     * Aggregates perfectly for a family whose sentence **is** the whole line: the modal header
     * `Choose one —` is identical on every card that prints it, which is why it sits at the top of
     * this ranking. It fragments completely for a family whose sentence **continues into an
     * arbitrary payload** — every spell-cast trigger's line ends in a different effect clause, so
     * that family splits into hundreds of rows of one to six cards each and never appears near the
     * top. In the implemented population this table read `Choose one —` at 100 cards and then
     * nothing above 27, with 504 cards of cast triggers invisible underneath it.
     *
     * So it systematically under-ranks exactly the families worth the most — the ones whose missing
     * construct is a prefix or a clause that the rest of the grammar continues past. That is the
     * opposite bias to [TOKEN]'s, and both are wrong in the same situation. [TAIL] is the one that
     * is not.
     */
    SHAPE {
        override fun of(line: LineResult, tailWords: Int) = skeleton(line.line)
    },

    /**
     * The parse's **tail**: the text from the decline's own offset onward, skeletonized and cut to
     * its first [tailWords] words.
     *
     * This is the ranking that has actually decided work. It is neither of the other two: it does
     * not name the token the line stopped at, and it does not name a sentence — it names **the
     * construct that would have to exist for the line to get further**, which is what a rule is
     * written for. A family here is a unit of work in the way a dead token is not, so
     * [FinenessReport.Decline.soleBlocked] and the explorer's unlock curve apply to it.
     *
     * The spell-cast trigger family is the worked example of why it was needed: 504 cards
     * sole-blocked against 263 for the next family, and *neither* other ranking showed it. [TOKEN]
     * smeared it across `you`, which "Whenever you gain life…" and "Whenever you attack…" also die
     * at; [SHAPE] shattered it across one row per payload. The band that closed it was picked with a
     * throwaway probe outside the tool, which is the reason this key exists in it now.
     *
     * **Why three words.** Measured over the corpus rather than chosen: at two, `When ~` swallows
     * several unrelated events into one family that names no single construct; at four and five the
     * families start splitting on the first word of the payload, which is precisely the fragmenting
     * [SHAPE] already does. Three is where a family names a construct and nothing more. It is a real
     * design parameter and it is re-measurable — `assay report --rank tail --tail-words N` prints
     * the table at any N, which is how it was picked.
     */
    TAIL {
        override fun of(line: LineResult, tailWords: Int): String {
            val decline = line.decline ?: return NO_DECLINE
            val rest = line.line.drop(decline.position.coerceIn(0, line.line.length)).trimStart()
            if (rest.isEmpty()) return "<end of line>"
            return firstWords(skeleton(rest), tailWords)
        }
    };

    abstract fun of(line: LineResult, tailWords: Int = TAIL_WORDS): String

    /**
     * Whether a family under this keying names a **piece of work**, which is what makes the
     * sole-blocked count and the unlock curve mean anything.
     *
     * False for [TOKEN] alone, and that restriction is a finding rather than a shortcut. Both those
     * numbers are claims of the form "write this and that many cards become covered". A sentence
     * shape is a unit of work — one rule reads every line of it — and so is a tail, which names the
     * construct the rule would be. A dead token is not: "every declined line of this card died at
     * `Whenever`" says nothing has been read and implies no rule. Computing it anyway produced a
     * curve claiming the top 400 token families cover 93% of Magic, against 15% for the top 400
     * shapes.
     */
    val namesWork: Boolean get() = this != TOKEN

    companion object {

        /** See [TAIL]'s KDoc: measured, not chosen. Overridable so it stays re-measurable. */
        const val TAIL_WORDS = 3

        /** `--rank <name>`, and the explorer's `by=` query parameter. */
        fun byName(name: String?): DeclineKey? =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) }

        /**
         * A line with the parts that differ between two printings of it collapsed: mana and tap
         * symbols to `{§}`, numbers to `#`. Self-reference is already abstracted to `~` by
         * [com.wingedsheep.assay.normalize.Normalizer], so a card's own name does not fragment
         * anything keyed through here.
         */
        fun skeleton(text: String): String = text.replace(SYMBOL, "{§}").replace(NUMBER, "#")

        /** The first [count] whitespace-separated words, marked as a prefix when it cut anything. */
        private fun firstWords(text: String, count: Int): String {
            val words = text.split(WHITESPACE)
            return if (words.size <= count) text else words.take(count).joinToString(" ") + " …"
        }

        /** A [LineResult] that is not a decline has no family; only a bug can put one here. */
        private const val NO_DECLINE = "<not declined>"

        private val SYMBOL = Regex("""\{[^}]*}""")
        private val NUMBER = Regex("""\b\d+\b""")
        private val WHITESPACE = Regex("""\s+""")
    }
}
