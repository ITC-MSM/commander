package com.wingedsheep.assay.explore

import com.wingedsheep.assay.corpus.ImplementedCorpus
import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleCorpus
import com.wingedsheep.assay.gate.CardResult
import com.wingedsheep.assay.gate.DeclineKey
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
 *
 * That split is deliberate and it is where the "a view, never a second source of truth" rule lands
 * here. The *keying* and the *counts* are [FinenessReport]'s, under [DeclineKey], so
 * `assay report --rank tail` and the page below are the same ranking by construction. What this adds
 * is a **link table** — which card names, a dozen example lines, the hand-written split — which is
 * not a number the gate has any use for and would only make its memory unbounded.
 */
class AssayIndex(
    val report: FinenessReport,
    /** Every keying's families, in the gate's own rank order, with the backlog joined onto each. */
    val families: Map<DeclineKey, List<DeclineFamily>>,
    val unlockCurves: Map<DeclineKey, List<Int>>,
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

    fun families(ranking: DeclineKey): List<DeclineFamily> = families[ranking].orEmpty()

    fun decline(key: String, ranking: DeclineKey): DeclineFamily? =
        families(ranking).firstOrNull { it.key == key }

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
            val backlog = DeclineKey.entries.associateWith { Backlog() }

            var seen = 0
            var fraction = 0.0
            for (card in OracleCorpus.cards(refresh = refresh, onProgress = { fraction = it })) {
                val result = touchstone.assay(card)
                fineness.add(result)
                cards.add(card)

                val declined = result.lines.filter { it.verdict == LineVerdict.DECLINED }
                val keys = DeclineKey.entries.associateWith { dimension ->
                    // Interned through the backlog's own key set, so a card's key list holds the
                    // same String instances the ranking does rather than 52,463 fresh copies.
                    declined.map { line -> backlog.getValue(dimension).intern(dimension.of(line)) }
                }
                rows.add(row(card, result, keys.mapValues { (_, list) -> list.distinct() }))
                attribution.observe(result)

                for ((dimension, list) in keys) {
                    val into = backlog.getValue(dimension)
                    list.forEachIndexed { index, key -> into.add(key, card.name, declined[index].line) }
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
            val report = fineness.build()
            val families = DeclineKey.entries.associateWith { dimension ->
                backlog.getValue(dimension).join(report.declines(dimension), goldens)
            }

            return AssayIndex(
                report = report,
                families = families,
                // The curve is a claim about work, so it exists exactly where a family names one —
                // see DeclineKey.namesWork for why the token ranking gives this question up.
                unlockCurves = DeclineKey.entries.filter { it.namesWork }.associateWith { dimension ->
                    UnlockCurve.of(families.getValue(dimension), rows, dimension)
                },
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

        private fun row(
            card: OracleCard,
            result: CardResult,
            declineKeys: Map<DeclineKey, List<String>>,
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
            declineKeys = if (declineKeys.values.all { it.isEmpty() }) emptyMap() else declineKeys,
        )
    }
}

/**
 * Cards covered after implementing the top *N* families in rank order, cumulatively.
 *
 * The number the sole-blocked column is to one family, this is to a *plan*: each declined card is
 * given the worst rank among its own families, so it joins the covered set at exactly the N where
 * its last blocker is reached. That is why the curve sits far below the sum of the cards-blocked
 * column, and why it is the one worth planning against.
 *
 * It exists only where [DeclineKey.namesWork] — the same restriction, for the same reason, as the
 * sole-blocked count itself.
 */
private object UnlockCurve {

    /** How far down the ranked list the curve is reported. Beyond this the tail is flat and long. */
    private const val CURVE_LENGTH = 400

    /** For N = 1..[CURVE_LENGTH]. Index 0 is N = 1. */
    fun of(families: List<DeclineFamily>, rows: List<CardRow>, dimension: DeclineKey): List<Int> {
        val rank = families.withIndex().associate { (index, family) -> family.key to index }
        val length = minOf(families.size, CURVE_LENGTH)
        val joiningAt = IntArray(length)
        for (row in rows) {
            val keys = row.declineKeys[dimension].orEmpty()
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

/**
 * The browsable half of one keying: which cards are behind each family, and a handful of its lines.
 *
 * Kept here rather than in [FinenessReport] on purpose. The counts are the gate's — this joins onto
 * them — but a card-name list per family is a *link table for a page*, and putting it in a report
 * whose whole cost model is "counters and bounded examples" would make a corpus run's memory grow
 * with the corpus for a question no gate asks.
 */
private class Backlog {

    private val keys = HashMap<String, String>()
    private val cards = LinkedHashMap<String, MutableSet<String>>()
    private val examples = LinkedHashMap<String, MutableSet<String>>()

    /** One String instance per family, so 35,000 card rows hold references rather than copies. */
    fun intern(key: String): String = keys.getOrPut(key) { key }

    fun add(key: String, cardName: String, line: String) {
        // Uncapped, for the reason the gate's own card sets are: this list is what the family page's
        // hand-written split and the feasibility probe are computed from, and a cap would silently
        // turn both into a measurement over the first 400 cards. Bounded by the declined line count.
        cards.getOrPut(key) { LinkedHashSet() }.add(cardName)
        examples.getOrPut(key) { LinkedHashSet() }.let { if (it.size < MAX_EXAMPLES) it.add(line) }
    }

    /** Joins onto the gate's ranked families, preserving its order and its counts. */
    fun join(ranked: List<FinenessReport.Decline>, goldens: Set<String>): List<DeclineFamily> =
        ranked.map { family ->
            val blocked = cards[family.key].orEmpty()
            DeclineFamily(
                key = family.key,
                cards = family.cards,
                lines = family.lines,
                soleBlocked = family.soleBlocked,
                implemented = blocked.count { it in goldens || it.substringBefore(" // ") in goldens },
                cardNames = blocked.toList(),
                examples = examples[family.key].orEmpty().toList(),
            )
        }

    private companion object {
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
    /** This card's distinct family keys under each keying. Empty when nothing declined. */
    val declineKeys: Map<DeclineKey, List<String>>,
) {
    fun declineKeys(dimension: DeclineKey): List<String> = declineKeys[dimension].orEmpty()
}

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
    val key: String,
    /** Cards that mention this family — how big the gap looks. */
    val cards: Int,
    val lines: Int,
    val implemented: Int,
    /**
     * Cards this family is the *only* blocker of — how big the gap actually is. Null under
     * [DeclineKey.TOKEN], where it would be well-defined and mean nothing.
     */
    val soleBlocked: Int?,
    /** Every blocked card, uncapped — the probe measures over all of them. Capped at the view. */
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
