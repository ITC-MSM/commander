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
    val cards: List<OracleCard>,
    val rows: List<CardRow>,
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

    fun decline(token: String): DeclineFamily? = declines.firstOrNull { it.token == token }

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

        /** Cards named per decline family, and lines shown as examples. Bounded, like the report's. */
        private const val MAX_CARDS_PER_FAMILY = 400
        private const val MAX_EXAMPLES_PER_FAMILY = 12

        /**
         * The sweep. One pass over the corpus, feeding the same [FinenessReport.Builder] the gate
         * uses so the explorer's headline numbers are the gate's numbers rather than a second
         * implementation that could disagree with it.
         *
         * @param progress called with cards-seen counts so the UI can show the sweep running instead
         *   of a blank page for five seconds.
         */
        fun build(refresh: Boolean = false, progress: (Int) -> Unit = {}): AssayIndex {
            val started = System.currentTimeMillis()
            val touchstone = Touchstone()
            val fineness = FinenessReport.builder()
            val attribution = RuleAttribution()

            val cards = mutableListOf<OracleCard>()
            val rows = mutableListOf<CardRow>()
            val familyLines = LinkedHashMap<String, Int>()
            val familyCards = LinkedHashMap<String, MutableSet<String>>()
            val familyExamples = LinkedHashMap<String, MutableSet<String>>()

            var seen = 0
            for (card in OracleCorpus.cards(refresh = refresh)) {
                val result = touchstone.assay(card)
                fineness.add(result)
                cards.add(card)
                rows.add(row(card, result))
                attribution.observe(result)

                for (line in result.lines) {
                    if (line.verdict != LineVerdict.DECLINED) continue
                    val token = line.declineToken ?: "<unknown>"
                    familyLines.merge(token, 1, Int::plus)
                    familyCards.getOrPut(token) { LinkedHashSet() }
                        .let { if (it.size < MAX_CARDS_PER_FAMILY) it.add(card.name) }
                    familyExamples.getOrPut(token) { LinkedHashSet() }
                        .let { if (it.size < MAX_EXAMPLES_PER_FAMILY) it.add(line.line) }
                }

                seen++
                if (seen % 2000 == 0) progress(seen)
            }
            progress(seen)

            // Cheap — reads the goldens' `// name` headers without decoding a single definition, so
            // the implemented/unimplemented split of every decline family costs one directory read.
            val goldens = runCatching { ImplementedCorpus.names() }.getOrDefault(emptySet())

            val declines = familyLines.map { (token, lines) ->
                val blocked = familyCards[token].orEmpty()
                DeclineFamily(
                    token = token,
                    cards = blocked.size,
                    lines = lines,
                    implemented = blocked.count { it in goldens || it.substringBefore(" // ") in goldens },
                    cardNames = blocked.toList(),
                    examples = familyExamples[token].orEmpty().toList(),
                )
            }.sortedWith(compareByDescending<DeclineFamily> { it.cards }.thenByDescending { it.lines })

            return AssayIndex(
                report = fineness.build(),
                declines = declines,
                cards = cards,
                rows = rows,
                ruleUsage = attribution.usage(),
                goldenNames = goldens,
                corpusFile = OracleCorpus.cacheFile().path,
                sweepMillis = System.currentTimeMillis() - started,
            )
        }

        private fun row(card: OracleCard, result: CardResult) = CardRow(
            name = card.name,
            setCode = card.setCode,
            layout = card.layout,
            faces = card.faces.size,
            lines = result.lines.size,
            roundTrips = result.roundTrips,
            covered = result.covered,
            inScope = result.inPhase1Scope,
            vanilla = card.isVanilla,
            declineTokens = result.lines.filter { it.verdict == LineVerdict.DECLINED }
                .mapNotNull { it.declineToken }.distinct(),
        )
    }
}

/**
 * One card's place in the sweep — everything a browsable table needs, and nothing that would make
 * 35,000 of them expensive to hold. The card's actual reading is re-assayed when someone opens it.
 */
data class CardRow(
    val name: String,
    val setCode: String?,
    val layout: String,
    val faces: Int,
    val lines: Int,
    val roundTrips: Boolean,
    val covered: Boolean,
    val inScope: Boolean,
    val vanilla: Boolean,
    val declineTokens: List<String>,
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
    val cards: Int,
    val lines: Int,
    val implemented: Int,
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
