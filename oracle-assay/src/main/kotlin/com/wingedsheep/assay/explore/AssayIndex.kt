package com.wingedsheep.assay.explore

import com.wingedsheep.assay.corpus.ImplementedCorpus
import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleCorpus
import com.wingedsheep.assay.gate.CardResult
import com.wingedsheep.assay.gate.FinenessReport
import com.wingedsheep.assay.gate.LineVerdict
import com.wingedsheep.assay.gate.Touchstone
import com.wingedsheep.assay.grammar.Grammar
import com.wingedsheep.assay.grammar.Steps
import com.wingedsheep.assay.grammar.Triggers
import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.assay.syntax.RuleShape
import java.util.Locale

/**
 * Everything the explorer serves that costs a whole-corpus sweep to know.
 *
 * Built **once** at startup and then read-only, for the reason the CLI runs the gate in one pass:
 * the sweep is where all the time goes, and a UI that re-ran it per request would be unusable. What
 * it keeps is deliberately not the parse trees — [FinenessReport] holds counters precisely so a
 * corpus run costs bounded memory, and this holds counters plus the thin index a page needs to link
 * a number back to the cards behind it. A card's actual reading is re-assayed on demand, which is
 * milliseconds for one card.
 *
 * The one thing here that the CLI reports cannot: **which cards are behind a decline family**, and
 * how many of those already have a hand-written golden. The report ranks the families; the point of
 * a browser is to click one and see the backlog it names.
 */
class AssayIndex(
    val report: FinenessReport,
    val declines: List<DeclineFamily>,
    val shapes: List<DeclineFamily>,
    val unlockCurves: Map<Ranking, List<Int>>,
    val cards: List<OracleCard>,
    val rows: List<CardRow>,
    /** Cards per [Companion.state] — the corpus in four buckets, which is the headline picture. */
    val stateCounts: Map<String, Int>,
    val ruleUsage: Map<Int, RuleUsage>,
    val goldenNames: Set<String>,
    val corpusFile: String,
    val sweepMillis: Long,
) {

    private val byName: Map<String, OracleCard> = buildMap {
        for (card in cards) {
            putIfAbsent(card.name.lowercase(Locale.ROOT), card)
            card.name.substringBefore(" // ").takeIf { it != card.name }
                ?.let { putIfAbsent(it.lowercase(Locale.ROOT), card) }
            for (face in card.faces) putIfAbsent(face.name.lowercase(Locale.ROOT), card)
        }
    }

    /** The join [com.wingedsheep.assay.gate.Differential] uses: Oracle ID first, name as fallback. */
    val oracleJoin: Map<String, OracleCard> = buildMap {
        for (card in cards) {
            card.oracleId?.let { putIfAbsent("id:$it", card) }
            putIfAbsent("name:${card.name.lowercase(Locale.ROOT)}", card)
            card.name.substringBefore(" // ").takeIf { it != card.name }
                ?.let { putIfAbsent("name:${it.lowercase(Locale.ROOT)}", card) }
        }
    }

    private val rowsByName: Map<String, CardRow> = rows.associateBy { it.name.lowercase(Locale.ROOT) }

    fun card(name: String): OracleCard? = byName[name.lowercase(Locale.ROOT)]

    fun row(name: String): CardRow? = rowsByName[name.lowercase(Locale.ROOT)]

    fun hasGolden(name: String): Boolean =
        name in goldenNames || name.substringBefore(" // ") in goldenNames

    fun families(ranking: Ranking): List<DeclineFamily> =
        if (ranking == Ranking.SHAPE) shapes else declines

    fun decline(token: String, ranking: Ranking): DeclineFamily? =
        families(ranking).firstOrNull { it.token == token }

    /**
     * Prefix-and-substring name search, ranked so an exact prefix wins.
     *
     * Deliberately not fuzzy: a card name typed most of the way is the query this answers, and a
     * ranked-by-edit-distance list of near misses is noise when the corpus has 35,000 entries whose
     * names share long prefixes ("Llanowar Elves" / "Llanowar Empath" / "Llanowar Envoy").
     */
    fun search(query: String, limit: Int = 25): List<OracleCard> {
        val needle = query.trim().lowercase(Locale.ROOT)
        if (needle.length < 2) return emptyList()
        val exact = mutableListOf<OracleCard>()
        val prefix = mutableListOf<OracleCard>()
        val contains = mutableListOf<OracleCard>()
        for (card in cards) {
            val name = card.name.lowercase(Locale.ROOT)
            when {
                name == needle -> exact.add(card)
                name.startsWith(needle) -> prefix.add(card)
                name.contains(needle) -> contains.add(card)
            }
            if (prefix.size + contains.size > limit * 8) break
        }
        return (exact + prefix.sortedBy { it.name.length } + contains.sortedBy { it.name.length }).take(limit)
    }

    companion object {

        /**
         * The sweep. One pass over the corpus, feeding the same [FinenessReport.Builder] the gate
         * uses so the explorer's headline numbers are the gate's numbers rather than a second
         * implementation that could disagree with it.
         *
         * @param progress cards seen so far, and how far through the bulk file that is, so the UI can
         *   show the sweep running instead of a blank page for five seconds. See
         *   [OracleCorpus.cards] for why the fraction is of bytes rather than of a card total.
         */
        fun build(refresh: Boolean = false, progress: (Int, Double) -> Unit = { _, _ -> }): AssayIndex {
            val started = System.currentTimeMillis()
            val touchstone = Touchstone()
            val fineness = FinenessReport.builder()
            val attribution = RuleAttribution()

            val cards = mutableListOf<OracleCard>()
            val rows = mutableListOf<CardRow>()
            val byToken = Grouping()
            val byShape = Grouping()

            var seen = 0
            var fraction = 0.0
            for (card in OracleCorpus.cards(refresh = refresh, onProgress = { fraction = it })) {
                val result = touchstone.assay(card)
                fineness.add(result)
                cards.add(card)

                val declined = result.lines.filter { it.verdict == LineVerdict.DECLINED }
                // Interned through the grouping's own key set, so a card's shape list holds the same
                // String instances the ranking does rather than 52,463 fresh copies.
                val tokens = declined.mapNotNull { it.declineToken }.distinct()
                val shapes = declined.map { skeleton(it.line) }.distinct()
                rows.add(row(card, result, tokens, shapes))
                attribution.observe(result)

                for ((index, line) in declined.withIndex()) {
                    byToken.add(line.declineToken ?: "<unknown>", card.name, line.line)
                    byShape.add(shapes.getOrElse(index) { skeleton(line.line) }, card.name, line.line)
                }

                seen++
                // Every 250 rather than every 2,000: the status poll is 700ms and the whole sweep is
                // ~5s, so a coarser tick makes a progress bar that moves in three visible jumps.
                if (seen % 250 == 0) progress(seen, fraction)
            }
            progress(seen, 1.0)

            // Cheap — reads the goldens' `// name` headers without decoding a single definition, so
            // the implemented/unimplemented split of every decline family costs one directory read.
            val goldens = runCatching { ImplementedCorpus.names() }.getOrDefault(emptySet())

            // Unlocks and the curve are computed for the SHAPE ranking only, and that restriction is
            // the finding rather than a shortcut. Both numbers are claims about *work*: "write this
            // and that many cards become covered". A sentence shape is a unit of work — one rule
            // reads every line of it. A dead token is not: a line dies at its first unknown token,
            // so "every declined line of this card died at `Whenever`" says nothing has been read
            // and implies no rule. Computing it anyway produced a curve claiming the top 400 token
            // families cover 93% of Magic, against 15% for the top 400 shapes. The token ranking
            // keeps its own honest question — what is the grammar missing — and gives up this one.
            val shapeFamilies = Unlocks.annotate(byShape.build(goldens), rows) { it.declineShapes }

            return AssayIndex(
                report = fineness.build(),
                declines = byToken.build(goldens),
                shapes = shapeFamilies,
                unlockCurves = mapOf(Ranking.SHAPE to Unlocks.curve(shapeFamilies, rows) { it.declineShapes }),
                cards = cards,
                rows = rows,
                stateCounts = rows.groupingBy(::state).eachCount(),
                ruleUsage = attribution.usage(),
                goldenNames = goldens,
                corpusFile = OracleCorpus.cacheFile().path,
                sweepMillis = System.currentTimeMillis() - started,
            )
        }

        /**
         * The four states a card can be in, which is the split the corpus bar and the card table
         * both use. **Vanilla is a covered state**, not a neutral one — a card with no rules text is
         * read completely and correctly, and colouring it like a decline was actively misleading
         * about a fifth of the corpus.
         */
        internal fun state(row: CardRow) = when {
            row.vanilla -> "vanilla"
            row.roundTrips -> "round-trip"
            row.covered -> "variant"
            else -> "declined"
        }

        /**
         * The sentence a declined line *is*, with the parts that differ between two printings of it
         * collapsed: mana and tap symbols to `{§}`, numbers to `#`. Self-reference is already
         * abstracted to `~` by [com.wingedsheep.assay.normalize.Normalizer], so a card's own name
         * does not fragment its shape.
         *
         * This is the second ranking, and it exists because the first one answers a different
         * question than the one someone choosing work is asking. A line dies on its *first* unknown
         * token, so a trigger whose prefix is already known dies somewhere after the comma while a
         * trigger whose prefix is unknown dies on "At" — one missing verb lands in several token
         * buckets, and a missing prefix looks larger than it is. Ranking by shape puts the whole
         * sentence in one row, which is the unit a rule is actually written for.
         */
        internal fun skeleton(line: String): String =
            line.replace(SYMBOL, "{§}").replace(NUMBER, "#")

        private val SYMBOL = Regex("""\{[^}]*}""")
        private val NUMBER = Regex("""\b\d+\b""")

        private fun row(
            card: OracleCard,
            result: CardResult,
            tokens: List<String>,
            shapes: List<String>,
        ) = CardRow(
            name = card.name,
            oracleId = card.oracleId,
            setCode = card.setCode,
            layout = card.layout,
            faces = card.faces.size,
            lines = result.lines.size,
            roundTrips = result.roundTrips,
            covered = result.covered,
            inScope = result.inPhase1Scope,
            vanilla = card.isVanilla,
            declineTokens = tokens,
            declineShapes = shapes,
        )
    }
}

/**
 * How the decline list is keyed.
 *
 * Two rankings because they answer two questions, and the module's guidance is explicit that they
 * disagree in ways that change what you write next. [TOKEN] is "what is the grammar missing";
 * [SHAPE] is "what sentence should I write a rule for".
 */
enum class Ranking { TOKEN, SHAPE }

/**
 * **Cards blocked is not cards unlocked**, and the gap between them is the most useful number here.
 *
 * A card is covered only when *every* one of its lines parses, so a family's card count says how
 * many cards mention it — not how many would come into coverage if it were written. The module's
 * own worked example: 410 cards decline on "At the beginning of…", and adding every step-trigger
 * prefix moved whole-card coverage by 23, because the other 387 were blocked on their effect clause
 * all along. A ranked list showing only the 410 sends you at the wrong work.
 *
 * Two derived numbers close that gap, and both are exact rather than estimated:
 *
 * - [DeclineFamily.unlocks] — cards this family is the *only* thing blocking. Write this one rule
 *   and exactly that many cards become covered.
 * - [AssayIndex.unlockCurve] — cards covered after implementing the top *N* families in rank order,
 *   cumulatively. Computed by giving each declined card the worst rank among its own families: a
 *   card joins the covered set at exactly the N where its last blocker is reached.
 *
 * Both are computed for [Ranking.SHAPE] only; see the note at the call site for why applying them to
 * dead tokens yields a number that is well-defined and means nothing.
 */
private object Unlocks {

    /** How far down the ranked list the curve is reported. Beyond this the tail is flat and long. */
    private const val CURVE_LENGTH = 400

    fun annotate(
        families: List<DeclineFamily>,
        rows: List<CardRow>,
        keysOf: (CardRow) -> List<String>,
    ): List<DeclineFamily> {
        val soleBlocker = HashMap<String, Int>()
        for (row in rows) {
            val keys = keysOf(row)
            if (keys.size == 1) soleBlocker.merge(keys.single(), 1, Int::plus)
        }
        return families.map { it.copy(unlocks = soleBlocker[it.token] ?: 0) }
    }

    /** Cards covered after the top *N* families, for N = 1..[CURVE_LENGTH]. Index 0 is N = 1. */
    fun curve(families: List<DeclineFamily>, rows: List<CardRow>, keysOf: (CardRow) -> List<String>): List<Int> {
        val rank = families.withIndex().associate { (index, family) -> family.token to index }
        val length = minOf(families.size, CURVE_LENGTH)
        val joiningAt = IntArray(length)
        for (row in rows) {
            val keys = keysOf(row)
            if (keys.isEmpty()) continue
            // The card becomes covered once its *last* remaining blocker is written; a card with any
            // blocker outside the reported prefix simply never joins within it.
            val last = keys.maxOf { rank[it] ?: Int.MAX_VALUE }
            if (last < length) joiningAt[last]++
        }
        var running = rows.count { it.covered }
        return joiningAt.map { running += it; running }
    }
}

/** Accumulates one keying of the declined lines: counts, the cards behind them, example lines. */
private class Grouping {

    private val lines = LinkedHashMap<String, Int>()
    private val cards = LinkedHashMap<String, MutableSet<String>>()
    private val examples = LinkedHashMap<String, MutableSet<String>>()

    fun add(key: String, cardName: String, line: String) {
        lines.merge(key, 1, Int::plus)
        // Uncapped, because this set is what the *ranking* is computed from — capping it made the
        // top of the list a plateau of families that all reported exactly the cap. It costs nothing:
        // the total number of (family, card) pairs is bounded by the number of declined lines.
        cards.getOrPut(key) { LinkedHashSet() }.add(cardName)
        examples.getOrPut(key) { LinkedHashSet() }.let { if (it.size < MAX_EXAMPLES) it.add(line) }
    }

    fun build(goldens: Set<String>): List<DeclineFamily> =
        lines.map { (key, count) ->
            val blocked = cards[key].orEmpty()
            DeclineFamily(
                token = key,
                cards = blocked.size,
                lines = count,
                implemented = blocked.count { it in goldens || it.substringBefore(" // ") in goldens },
                // The *shown* list is bounded — a page does not need 900 names — but the count above
                // is the real one, and the page says so when it is showing fewer.
                cardNames = blocked.take(MAX_SHOWN_CARDS),
                examples = examples[key].orEmpty().toList(),
            )
        }.sortedWith(compareByDescending<DeclineFamily> { it.cards }.thenByDescending { it.lines })

    private companion object {
        const val MAX_SHOWN_CARDS = 400
        const val MAX_EXAMPLES = 12
    }
}

/**
 * One card's place in the sweep — everything a browsable table needs, and nothing that would make
 * 35,000 of them expensive to hold. The card's actual reading is re-assayed when someone opens it.
 */
data class CardRow(
    val name: String,
    /** Carried so the set filter can join on Oracle ID rather than on name. See `SetMembership`. */
    val oracleId: String?,
    /** The card's *representative* printing — what Scryfall shows it under, not where it was printed. */
    val setCode: String?,
    val layout: String,
    val faces: Int,
    val lines: Int,
    val roundTrips: Boolean,
    val covered: Boolean,
    val inScope: Boolean,
    val vanilla: Boolean,
    val declineTokens: List<String>,
    val declineShapes: List<String>,
)

/**
 * A decline family with the backlog behind it.
 *
 * [implemented] is the split the module's guidance calls the fastest route to full coverage: a
 * declined line on a card that already has a hand-written golden is a **grammar** gap whose
 * known-good answer is already written and which the differential confirms the moment it parses,
 * while a declined line on a card nobody has implemented may be an **SDK** gap with a much longer
 * lead time. `assay report --implemented` answers that by re-running the whole sweep over a filtered
 * population; carrying the count per family answers it for every family at once.
 */
data class DeclineFamily(
    val token: String,
    /** Cards that mention this family — how big the gap looks. */
    val cards: Int,
    val lines: Int,
    val implemented: Int,
    /** Cards this family is the *only* blocker of — how big the gap actually is. See [Unlocks]. */
    val unlocks: Int = 0,
    val cardNames: List<String>,
    val examples: List<String>,
)

/** How many corpus lines and cards a single grammar rule was the one to print. */
data class RuleUsage(val lines: Int, val cards: Int)

/**
 * Which rule printed what, counted over the corpus.
 *
 * The kernel does not record parse provenance — a reading is a value, and the rule that produced it
 * is gone by the time the gate sees it. But the *printing* side is deterministic and defined:
 * [com.wingedsheep.assay.syntax.oneOf] prints through the first canonical alternative that can
 * express the value, so "the rule that would print this ability" is an exact question with an exact
 * answer, and it is the same answer the touchstone's round trip depends on.
 *
 * That is what this counts, and it is why the number is honest rather than indicative: a rule with
 * zero usage is a rule that never printed anything in 34,882 cards.
 */
private class RuleAttribution {

    private val lines = HashMap<Int, Int>()
    private val cards = HashMap<Int, MutableSet<String>>()

    fun observe(result: CardResult) {
        for (line in result.lines) {
            val fragment = line.model ?: continue
            for (ability in fragment.keywordAbilities) {
                credit(attribute(Grammar.keywordAbility, ability), result.card.name)
            }
            if (fragment.script.spellEffect != null) {
                credit(attribute(Steps.step, fragment.script), result.card.name)
            }
            for (trigger in fragment.script.triggeredAbilities) {
                credit(attribute(Triggers.trigger, trigger), result.card.name)
            }
        }
    }

    private fun credit(rule: Phrase<*>?, cardName: String) {
        val id = rule?.id ?: return
        lines.merge(id, 1, Int::plus)
        cards.getOrPut(id) { HashSet() }.add(cardName)
    }

    fun usage(): Map<Int, RuleUsage> =
        lines.mapValues { (id, count) -> RuleUsage(lines = count, cards = cards[id]?.size ?: 0) }

    private companion object {

        /**
         * The concrete rule an alternation would delegate printing to, following the same
         * first-canonical-that-can-print walk [com.wingedsheep.assay.syntax.oneOf] uses.
         *
         * Stops at a template or a leaf, because a template's slot values cannot be recovered from
         * the whole value without re-matching — and re-matching to attribute a number would be a
         * second, unverified implementation of the print side. The three entry points this is called
         * with are all alternations over leaf rules, which is exactly the level the numbers are for.
         */
        @Suppress("UNCHECKED_CAST")
        fun attribute(root: Phrase<*>, value: Any?): Phrase<*>? {
            if ((root as Phrase<Any?>).unparse(value) == null) return null
            val shape = root.shape as? RuleShape.Choice ?: return root
            val branch = shape.alternatives
                .firstOrNull { it.canonical && (it as Phrase<Any?>).unparse(value) != null }
            return if (branch == null) root else attribute(branch, value)
        }
    }
}
