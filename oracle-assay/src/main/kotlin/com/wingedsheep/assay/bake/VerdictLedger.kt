package com.wingedsheep.assay.bake

import com.wingedsheep.assay.compile.CardCompiler
import com.wingedsheep.assay.compile.CompileResult
import com.wingedsheep.assay.corpus.OracleCard
import com.wingedsheep.assay.corpus.OracleCorpus
import com.wingedsheep.assay.gate.Touchstone
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * One sorted line per card: can Assay read this card *whole*, and if not, what stopped it.
 *
 * ## Two jobs, one artifact, and that is the point
 *
 * **It is the coverage view's data source.** The Set Completion page wants to say "this card is
 * missing, and Assay already reads it end to end" — which is the cheapest possible backlog signal,
 * because a card in that state needs no new grammar and no new SDK vocabulary. Computing it live is
 * not available there: the production `game-server` is a bare JRE with no `~/.cache/scryfall`, so a
 * sweep would have to download 24 MB at boot for a page most visitors never open. The same problem
 * `scripts/gen-set-totals` solves for the denominator, solved the same way — bake it into a
 * classpath resource and join at request time.
 *
 * **It is also the regression ledger this module has been asking for.** `AGENTS.md` names the gap
 * outright: at corpus size a change can move thousands of verdicts, and "round-trips went up" hides
 * the twelve cards that went *down*; the shape that fits this repo is "a committed ledger — one
 * sorted line per card, verdict plus decline token, pinned to the Scryfall bulk's version —
 * re-blessed deliberately like the card goldens, so every PR shows exactly which cards changed
 * reading". That is this file. Sorting by name and giving each card its own line is therefore not
 * formatting preference: it is what makes `git diff` after a re-bake read as the list of cards whose
 * reading changed.
 *
 * Because it is both, the two uses constrain each other usefully. The view needs it accurate, so it
 * must be re-baked when the grammar moves; the ledger needs it re-blessed deliberately, so it must
 * *not* be regenerated silently as part of a build. `just assay-bake` is a human action, and a stale
 * resource degrades into an out-of-date badge rather than a wrong gate.
 *
 * ## Why the compiler and not the touchstone
 *
 * "Could be implemented using Assay" is exactly [CardCompiler]'s question, so it is exactly
 * [CardCompiler] that answers it — the same fail-closed path the Scenario Builder sandbox plays a
 * card through. Line verdicts alone would over-report: a card whose every line round-trips can still
 * fail on a `*` power, a second face, or `CardValidator`, and a badge saying "ready" for a card that
 * cannot actually be produced would be worse than no badge.
 */
object VerdictLedger {

    /**
     * One card's reading. A row with no [kind] is a card Assay reads whole — the common case worth
     * optimising the encoding for, and the only state the coverage badge treats as "ready".
     */
    @Serializable
    data class Row(
        val name: String,
        /** The [com.wingedsheep.assay.compile.DeclineKind] that stopped it, or null when it compiled. */
        val kind: String? = null,
        /** The printed line the decline points at, truncated. Null when the decline is card-wide. */
        val line: String? = null,
    )

    /**
     * The ledger. [corpus] and [whole] are stored rather than derived so a reader can report the
     * headline without walking 35,000 rows, and so a diff shows the totals moving on line two.
     */
    @Serializable
    data class Ledger(
        /** Cards assayed. */
        val corpus: Int,
        /** Cards [CardCompiler] read whole. */
        val whole: Int,
        /** Every card, sorted by name — see the class KDoc on why the sort is load-bearing. */
        val cards: List<Row>,
    )

    /**
     * Sweep the corpus and build the ledger.
     *
     * @param refresh re-download the Scryfall bulk first.
     * @param limit assay only the first N cards — a smoke run, never something to commit.
     * @param onProgress cards done so far, called every [PROGRESS_EVERY] cards.
     */
    fun build(
        refresh: Boolean = false,
        limit: Int? = null,
        onProgress: ((Int) -> Unit)? = null,
    ): Ledger {
        val touchstone = Touchstone()
        val rows = mutableListOf<Row>()
        var whole = 0
        var seen = 0
        var sequence = OracleCorpus.cards(refresh = refresh)
        if (limit != null) sequence = sequence.take(limit)
        for (card in sequence) {
            rows += row(card, touchstone).also { if (it.kind == null) whole++ }
            seen++
            if (onProgress != null && seen % PROGRESS_EVERY == 0) onProgress(seen)
        }
        // Sorted by the same key the server looks a card up by, so the resource is a diff and the
        // server's map build is a single pass with no re-ordering.
        rows.sortBy { it.name }
        return Ledger(corpus = seen, whole = whole, cards = rows)
    }

    private fun row(card: OracleCard, touchstone: Touchstone): Row =
        when (val result = CardCompiler.compile(card, touchstone)) {
            is CompileResult.Compiled -> Row(card.name)
            is CompileResult.Declined -> {
                // The first decline is the one to report: the compiler accumulates every reason, but
                // a badge has room for one and the first is the earliest thing that would have to be
                // fixed. The rest stay reachable through `assay explain` on the live grammar.
                val first = result.declines.firstOrNull()
                Row(
                    name = card.name,
                    kind = first?.kind?.name ?: "UNKNOWN",
                    line = first?.line?.let { it.take(LINE_CAP).trim() },
                )
            }
        }

    /**
     * Write the ledger as the classpath resource `game-server` reads.
     *
     * Framed by hand rather than by `prettyPrint`, for the one property that makes this file a
     * regression check: **exactly one card per line**. `kotlinx`'s pretty printer puts every field on
     * its own line, which turns 35,000 cards into 160,000 lines and a one-card change into a
     * five-line diff hunk that no longer reads as "this card's verdict moved". Compact-encoding each
     * row and joining with newlines gives a file `git diff` reports card by card, and halves it.
     *
     * The result is still ordinary JSON — the newlines are inside an array, where whitespace is
     * insignificant — so the server decodes it with a plain `Json` and knows nothing about the
     * framing.
     */
    fun write(ledger: Ledger, target: File) {
        target.parentFile?.mkdirs()
        target.bufferedWriter().use { out ->
            out.write("{\n")
            out.write("\"corpus\": ${ledger.corpus},\n")
            out.write("\"whole\": ${ledger.whole},\n")
            out.write("\"cards\": [\n")
            ledger.cards.forEachIndexed { i, row ->
                out.write(JSON.encodeToString(Row.serializer(), row))
                if (i < ledger.cards.lastIndex) out.write(",")
                out.write("\n")
            }
            out.write("]}\n")
        }
    }

    /**
     * Where `just assay-bake` writes, relative to the repo root: the `game-server` classpath
     * resource `SetCoverageService` joins into the per-card DTOs. Named here rather than in the
     * justfile so the CLI's `--out` default and the server's `ClassPathResource` path have one
     * obvious place to be compared.
     */
    const val DEFAULT_OUTPUT = "game-server/src/main/resources/coverage/assay-verdicts.json"

    /**
     * How much of a declined line is kept. Long enough to recognise the sentence in a tooltip, short
     * enough that 30,000 of them stay a resource rather than a corpus copy.
     */
    private const val LINE_CAP = 120

    private const val PROGRESS_EVERY = 2_000

    /** Compact: [write] does the framing, one row per line. `encodeDefaults = false` drops the nulls. */
    private val JSON = Json { encodeDefaults = false }
}
