package com.wingedsheep.assay.gate

import com.wingedsheep.assay.corpus.ImplementedCard
import com.wingedsheep.assay.corpus.ImplementedCorpus
import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleCorpus
import com.wingedsheep.assay.corpus.OracleFace
import com.wingedsheep.assay.normalize.Normalizer
import com.wingedsheep.assay.grammar.CardFragment
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.serialization.CardSerialization
import java.util.Locale

/**
 * Gate 2 — the **differential**: Assay's reading of a card against the definition a human wrote
 * from the same text.
 *
 * The touchstone ([Touchstone]) proves a parse is reversible. It structurally cannot prove the parse
 * is *right*: a rule that reads "Elves" as `Elve`, or reads a negative condition as its positive,
 * round-trips perfectly and means the wrong thing. This gate is the general answer to that class,
 * and it runs on an asset the incumbent pipeline never had — [ImplementedCorpus], the hand-written
 * cards themselves.
 *
 * ## Fail-closed scoping
 *
 * A comparison is only run where Assay has a **complete** reading of the card: every ability line
 * either round-trips or is a normalized variant ([CardResult.covered]). Comparing a partially-read
 * card would be fail-open — a keyword Assay never saw because its line declined would look like
 * agreement, and the gate would report confidence it has not earned. Everything else is counted
 * into a named [Population] bucket instead, so the denominator is always visible.
 *
 * ## What is compared, at this stage
 *
 * The card's own printed **keyword abilities** — which is exactly the class Phase 1's grammar reads
 * whole, and needs no new grammar to start finding bugs. The SDK spells them two ways
 * ([CardDefinition.keywords] for the parameterless ones, [CardDefinition.keywordAbilities] for the
 * rest), so the hand-written side is unified into one set before comparing; see [printedKeywords].
 *
 * As the grammar grows, the comparison grows with it — triggered abilities, then spell effects —
 * and the scoping rule above is what keeps each addition honest.
 */
class Differential(private val touchstone: Touchstone = Touchstone()) {

    /**
     * Run the gate over every hand-written card.
     *
     * The Scryfall side is indexed in memory (thin [OracleCard]s, ~35k of them) and the hand-written
     * side is streamed, so only one [CardDefinition] is resident at a time — the goldens are 20 MB
     * of JSON and inflating all of them at once is the one way this gate could need real heap.
     */
    fun run(refresh: Boolean = false, limit: Int? = null, setFilter: String? = null): DifferentialReport {
        val oracle = indexOracle(refresh)
        val builder = DifferentialReport.builder()
        var seen = 0
        for (implemented in ImplementedCorpus.cards()) {
            if (setFilter != null && !implemented.setCode.equals(setFilter, ignoreCase = true)) continue
            builder.add(compare(implemented, oracle))
            seen++
            if (limit != null && seen >= limit) break
        }
        return builder.build()
    }

    /**
     * Index by Oracle ID *and* by name. The ID is the reliable join — it is what Scryfall considers
     * one card across every printing — and the name is the fallback for goldens minted before the id
     * was recorded, or for the setless `custom/` cards that have no Scryfall entry at all.
     */
    private fun indexOracle(refresh: Boolean): Map<String, OracleCard> {
        val index = HashMap<String, OracleCard>()
        for (card in OracleCorpus.cards(refresh = refresh)) {
            card.oracleId?.let { index.putIfAbsent("id:$it", card) }
            index.putIfAbsent("name:${card.name.lowercase(Locale.ROOT)}", card)
            // Split and DFC names join on either half; the front face is what a golden is named for.
            card.name.substringBefore(" // ").takeIf { it != card.name }
                ?.let { index.putIfAbsent("name:${it.lowercase(Locale.ROOT)}", card) }
        }
        return index
    }

    fun compare(implemented: ImplementedCard, oracle: Map<String, OracleCard>): CardComparison {
        val definition = implemented.definition
            ?: return CardComparison(implemented, null, Population.UNDECODABLE)

        val card = definition.oracleId?.let { oracle["id:$it"] }
            ?: oracle["name:${implemented.name.lowercase(Locale.ROOT)}"]
            ?: return CardComparison(implemented, null, Population.NO_ORACLE_TEXT)

        // Multi-face cards split their model across `backFace` / `cardFaces`, which the keyword
        // comparison would have to mirror face by face. Excluded and counted rather than compared
        // against the front face alone, which would report a divergence for every back-face keyword.
        if (card.faces.size > 1) return CardComparison(implemented, card, Population.MULTI_FACE)

        // The comparison is only meaningful if both sides are talking about the same text. A golden
        // carries the Oracle text it was authored from, so disagreeing with Scryfall means either
        // the name join found the wrong card or the card was written against wording that has since
        // changed — and in both cases Assay would be reading one card and diffing another. Found by
        // the gate itself: three cards joined to an entry with *no* text, which Assay covers
        // trivially, and then "diverged" against a fully-implemented script.
        //
        // Compared *normalized*, not raw. Goldens include printed reminder text inconsistently —
        // "Flying" in one and "Flying (This creature can't be blocked…)" in another — and that is
        // authoring noise, not a difference in what the card says. Normalization strips reminders as
        // an invertible pass, so reusing it here compares the two texts on the only terms the
        // grammar ever sees them.
        if (!sameText(card.faces.single(), definition)) {
            return CardComparison(implemented, card, Population.ORACLE_TEXT_DIFFERS)
        }

        val result = touchstone.assay(card)
        if (!result.covered) return CardComparison(implemented, card, Population.NOT_COVERED)

        // The other half of fail-closed scoping. Assay reading every *line* is not the same as Assay
        // modelling every *slot*: a keyword the SDK lowers to a triggered ability at authoring time
        // (rampage, bushido, modular) leaves content in a slot the grammar cannot produce, and
        // confirming such a card would be claiming to have checked a lowering nobody compared.
        // Stated as "everything outside the modelled slots is still default", so widening the
        // grammar is one edit here and the check tightens with it.
        if (unmodelledSlots(definition.script) != CardScript.EMPTY) {
            return CardComparison(implemented, card, Population.SCRIPT_NOT_MODELLED)
        }

        val fromText = result.lines.mapNotNull { it.model }.fold(CardFragment.EMPTY, CardFragment::merge)
        val fromCard = CardFragment(
            keywordAbilities = printedKeywords(definition).toList(),
            script = modelledSlots(definition.script),
        )

        val textKeywords = Folds.apply(fromText.keywordAbilities.toSet())
        val cardKeywords = Folds.apply(fromCard.keywordAbilities.toSet())
        val scriptsAgree = normalizeSlotNames(fromText.script) == normalizeSlotNames(fromCard.script)

        return if (textKeywords == cardKeywords && scriptsAgree) {
            CardComparison(implemented, card, Population.COMPARED, Verdict.CONFIRMED)
        } else {
            CardComparison(
                implemented = implemented,
                oracle = card,
                population = Population.COMPARED,
                verdict = Verdict.DIVERGENT,
                onlyInText = (textKeywords - cardKeywords).toList(),
                onlyInCard = (cardKeywords - textKeywords).toList(),
                textScript = fromText.script.takeUnless { scriptsAgree },
                cardScript = fromCard.script.takeUnless { scriptsAgree },
            )
        }
    }

    /**
     * Do the Scryfall face and the golden say the same thing, once reminder text is out of the way?
     *
     * The golden's `oracleText` is run through the same [Normalizer] as the printed face, so the
     * two are compared as ability lines rather than as bytes. Only the text matters here, but the
     * face is built with the golden's own name so that self-reference abstraction lines up.
     */
    private fun sameText(face: OracleFace, definition: CardDefinition): Boolean {
        val golden = OracleFace(name = face.name, oracleText = definition.oracleText, typeLine = face.typeLine)
        return Normalizer.normalize(golden).lines == Normalizer.normalize(face).lines
    }

    /**
     * The `CardScript` slots the grammar can produce — [CardFragment.MODELLED_SLOTS_NOTE] — and its
     * complement. Written as a `copy` pair rather than a field list so the two stay exhaustive
     * between them however many fields `CardScript` grows.
     */
    private fun modelledSlots(script: CardScript) =
        CardScript(spellEffect = script.spellEffect, targetRequirements = script.targetRequirements)

    private fun unmodelledSlots(script: CardScript) =
        script.copy(spellEffect = null, targetRequirements = emptyList())

    /**
     * Rename target slots to their position, on both sides, before comparing.
     *
     * The string linking a `TargetRequirement` to the `EffectTarget` reading it is arbitrary — see
     * [com.wingedsheep.assay.grammar.Targets] — so two models differing only in that name are the
     * same model, and a differential that reported them as divergent would be measuring a naming
     * convention. Done textually over the serialized script, which is how
     * `CardDefinitionSnapshotTest.normalizeAbilityIds` solves the identical problem for ability ids;
     * a structural rewrite would have to walk every `Effect` subtype to find the references.
     */
    internal fun normalizeSlotNames(script: CardScript): String {
        var json = CardSerialization.json.encodeToString(CardScript.serializer(), script)
        // Replace the declared ids specifically, rather than every `"id"`/`"name"` key: those keys
        // occur all over an effect tree, and rewriting one that is not a target slot would make two
        // genuinely different models compare equal — a fail-open normalization, which is the one
        // kind of bug a gate must never contain.
        script.targetRequirements.mapNotNull { it.id }.distinct().forEachIndexed { index, id ->
            json = json.replace("\"$id\"", "\"slot_$index\"")
        }
        return json
    }

    /**
     * The hand-written side, as one set.
     *
     * `CardDefinition` carries a card's printed keywords in two fields — [CardDefinition.keywords]
     * holds the parameterless ones as bare `Keyword` constants and [CardDefinition.keywordAbilities]
     * holds the parameterized ones — while Assay parses everything into `KeywordAbility`. Lifting
     * the first into `KeywordAbility.Simple` is what makes the two sides comparable at all.
     *
     * That the SDK needs the lift is itself worth noticing: two spellings of one concept is the same
     * shape as the `PROTECTION_FROM_EACH_OPPONENT` finding Phase 1 reported.
     */
    internal fun printedKeywords(definition: CardDefinition): Set<KeywordAbility> =
        definition.keywords.map { KeywordAbility.Simple(it) }.toSet() + definition.keywordAbilities.toSet()
}

/**
 * The **fold list**: representations that are known to be equivalent, normalized away before the two
 * sides are compared.
 *
 * The design is explicit that this list is reviewed and never grown silently, because every entry is
 * a divergence the gate stops reporting — a fold added carelessly is how a semantic gate quietly
 * turns into a formality. Each one therefore has to say *why* it is not a difference, and the bar is
 * that both spellings are already agreed to mean the same thing somewhere outside this file.
 */
internal object Folds {

    fun apply(abilities: Set<KeywordAbility>): Set<KeywordAbility> = dropImpliedSimpleMarkers(abilities)

    /**
     * **A parameterless marker implied by a parameterized ability of the same keyword.**
     *
     * `CardDefinition` carries a parameterized keyword twice by design, and the SDK says so itself:
     * [KeywordAbility.keyword] is documented as existing "to automatically populate
     * `CardDefinition.keywords` so that parameterized keyword abilities (e.g., Ward {1}) are visible
     * in the base keyword set". So the parameter lives in `keywordAbilities` and a bare constant
     * lives in `keywords` — Punk Frogs, Frogmite and Teeka's Dragon are all written that way. The
     * Oracle text says it once, Assay reads it once, and the second copy is an index entry rather
     * than a second ability.
     *
     * Dropping the marker *only when a parameterized ability names the same keyword* is what keeps
     * this narrow: a card carrying a bare `WARD` and nothing else still diverges, which is the case
     * that would be a real bug.
     */
    private fun dropImpliedSimpleMarkers(abilities: Set<KeywordAbility>): Set<KeywordAbility> {
        val parameterized = abilities.filter { it !is KeywordAbility.Simple }.mapNotNull { it.keyword }.toSet()
        if (parameterized.isEmpty()) return abilities
        return abilities.filterNot { it is KeywordAbility.Simple && it.keyword in parameterized }.toSet()
    }
}

/** Why a hand-written card was or was not compared. The denominator is never hidden. */
enum class Population {
    /** Compared — Assay read the whole card and the golden decoded. */
    COMPARED,

    /** Assay does not yet read every line of this card. Not a bug; the grammar has not reached it. */
    NOT_COVERED,

    /** Multi-face: out of scope for the keyword comparison, see [Differential.compare]. */
    MULTI_FACE,

    /**
     * Assay reads every line, but the hand-written card puts content in a `CardScript` slot the
     * grammar cannot yet produce — typically a keyword the SDK lowers to a triggered ability at
     * authoring time. Not compared, because confirming it would claim a check nobody performed.
     */
    SCRIPT_NOT_MODELLED,

    /** No Scryfall Oracle entry joined — a `custom/` card, or a name the index does not carry. */
    NO_ORACLE_TEXT,

    /**
     * An entry joined, but its Oracle text is not the text the golden was authored from. Either the
     * name join found the wrong card or the wording has changed since. Not compared: Assay would be
     * reading one card and diffing another.
     */
    ORACLE_TEXT_DIFFERS,

    /** The golden JSON would not decode. Always a bug, in the SDK or in a stale snapshot. */
    UNDECODABLE,
}

enum class Verdict {
    /** Assay's model and the hand-written model agree. */
    CONFIRMED,

    /** They disagree. Either a parser bug or a bug in the hand-written card — both worth finding. */
    DIVERGENT,
}

data class CardComparison(
    val implemented: ImplementedCard,
    val oracle: OracleCard?,
    val population: Population,
    val verdict: Verdict? = null,
    /** Keyword abilities Assay read from the text that the hand-written card does not carry. */
    val onlyInText: List<KeywordAbility> = emptyList(),
    /** Keyword abilities the hand-written card carries that Assay did not read from the text. */
    val onlyInCard: List<KeywordAbility> = emptyList(),
    /** Set only when the scripts disagree: Assay's reading, and the hand-written one. */
    val textScript: CardScript? = null,
    val cardScript: CardScript? = null,
)

/**
 * The differential's numbers, in the shape [FinenessReport] uses: counters plus bounded examples,
 * so a whole-corpus run costs a fixed amount of memory.
 */
class DifferentialReport private constructor(
    val cards: Int,
    val byPopulation: Map<Population, Int>,
    val confirmed: Int,
    val divergent: Int,
    val divergences: List<CardComparison>,
    val undecodable: List<String>,
) {

    /** Compared cards that agreed, in parts per thousand — the differential's own fineness. */
    val agreement: Double get() = FinenessReport.permil(confirmed, confirmed + divergent)

    /**
     * The gate is red on a golden that will not decode, never on a divergence.
     *
     * A divergence is a *finding* — it has to be read and classified as parser bug, card bug, or
     * known fold, and until someone has done that, failing the build would only teach people to
     * ignore it. The MVP's acceptance is "every divergence classified", which is a human's verdict,
     * not a counter's.
     */
    val clean: Boolean get() = undecodable.isEmpty()

    fun render(topDivergences: Int = 40): String = buildString {
        appendLine("Argentum Assay — differential (${CardFragment.MODELLED_SLOTS_NOTE}, keywords)")
        appendLine("=".repeat(78))
        appendLine()
        appendLine(row("Hand-written cards", cards.toString()))
        appendLine(row("  compared", pop(Population.COMPARED).toString()))
        appendLine(row("  not yet covered by the grammar", pop(Population.NOT_COVERED).toString()))
        appendLine(row("  script slot not modelled yet", pop(Population.SCRIPT_NOT_MODELLED).toString()))
        appendLine(row("  multi-face (out of scope)", pop(Population.MULTI_FACE).toString()))
        appendLine(row("  no Scryfall Oracle entry", pop(Population.NO_ORACLE_TEXT).toString()))
        appendLine(row("  Oracle text differs from golden", pop(Population.ORACLE_TEXT_DIFFERS).toString()))
        appendLine(row("  golden would not decode", "${pop(Population.UNDECODABLE)}   ${note(undecodable)}"))
        appendLine()
        appendLine(row("Confirmed — models agree", "$confirmed   ${permilText(agreement)}"))
        appendLine(row("DIVERGENT — read every one", divergent.toString()))

        if (undecodable.isNotEmpty()) {
            appendLine()
            appendLine("GOLDENS THAT WOULD NOT DECODE (must be 0)")
            appendLine("-".repeat(78))
            undecodable.forEach { appendLine("  $it") }
        }

        appendLine()
        appendLine("DIVERGENCES — each is a parser bug, a card bug, or a fold. Classify all of them.")
        appendLine("-".repeat(78))
        if (divergences.isEmpty()) {
            appendLine("  (none)")
        } else {
            divergences.take(topDivergences).forEach { d ->
                appendLine("  ${d.implemented.name}  [${d.implemented.setCode}]")
                if (d.onlyInText.isNotEmpty()) {
                    appendLine("    text has, card lacks:  ${d.onlyInText.joinToString(", ", transform = ::structural)}")
                }
                if (d.onlyInCard.isNotEmpty()) {
                    appendLine("    card has, text lacks:  ${d.onlyInCard.joinToString(", ", transform = ::structural)}")
                }
                if (d.textScript != null || d.cardScript != null) {
                    appendLine("    script from text:      ${d.textScript?.let(::structural) ?: "(none)"}")
                    appendLine("    script on the card:    ${d.cardScript?.let(::structural) ?: "(none)"}")
                }
            }
            if (divergent > divergences.size) {
                appendLine("  … and ${divergent - divergences.size} more (examples are capped)")
            } else if (divergences.size > topDivergences) {
                appendLine("  … and ${divergences.size - topDivergences} more; raise --top to see them")
            }
        }
    }

    private fun pop(p: Population) = byPopulation[p] ?: 0

    /**
     * A divergence row shows the **structure**, not `KeywordAbility.description`.
     *
     * The prose is what makes a divergence unreadable exactly where it matters most: where the SDK
     * spells one concept two ways, both sides describe themselves as "Flanking" and the row looks
     * like a tool bug. `toString()` on the data class distinguishes `Simple(keyword=FLANKING)` from
     * the `Flanking` object, which is the whole finding.
     */
    private fun structural(ability: KeywordAbility) = ability.toString()

    /**
     * Scripts print as their serialized form rather than as `toString()`. An effect tree's
     * `toString()` is a wall of nested data-class names that is unreadable at the width a report
     * row has; the JSON is the same shape a golden shows, so a divergence row can be compared
     * against the golden directly.
     */
    private fun structural(script: CardScript) =
        CardSerialization.json.encodeToString(CardScript.serializer(), script)
    private fun note(items: List<*>) = if (items.isEmpty()) "" else "<- READ THESE"
    private fun row(label: String, value: String) = "  %-34s %s".format(Locale.ROOT, label, value).trimEnd()
    private fun permilText(value: Double) = "%.1f‰ (%.1f%%)".format(Locale.ROOT, value, value / 10.0)

    companion object {
        fun builder() = Builder()
    }

    class Builder {
        private var cards = 0
        private var confirmed = 0
        private var divergent = 0
        private val byPopulation = mutableMapOf<Population, Int>()
        private val divergences = mutableListOf<CardComparison>()
        private val undecodable = mutableListOf<String>()

        fun add(comparison: CardComparison) = apply {
            cards++
            byPopulation.merge(comparison.population, 1, Int::plus)
            when (comparison.verdict) {
                Verdict.CONFIRMED -> confirmed++
                Verdict.DIVERGENT -> {
                    divergent++
                    if (divergences.size < MAX_EXAMPLES) divergences.add(comparison)
                }

                null -> Unit
            }
            if (comparison.population == Population.UNDECODABLE && undecodable.size < MAX_EXAMPLES) {
                undecodable.add("${comparison.implemented.name} [${comparison.implemented.setCode}]")
            }
        }

        fun build() = DifferentialReport(
            cards = cards,
            byPopulation = byPopulation.toMap(),
            confirmed = confirmed,
            divergent = divergent,
            divergences = divergences.toList(),
            undecodable = undecodable.toList(),
        )

        private companion object {
            /**
             * Divergences are meant to be read one by one, so the cap is generous where the
             * fineness report's is tight — but it is still a cap, because a systematic parser bug
             * would otherwise print thousands of identical rows and bury the interesting ones.
             */
            const val MAX_EXAMPLES = 300
        }
    }
}
