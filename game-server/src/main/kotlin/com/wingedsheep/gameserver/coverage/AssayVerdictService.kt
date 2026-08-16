package com.wingedsheep.gameserver.coverage

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

/**
 * Reads the baked Argentum Assay verdict ledger — "can Assay read this card whole?", per card name.
 *
 * ## Why baked and not computed
 *
 * Assay's answer is a function of the grammar plus Scryfall's Oracle bulk, and this server has the
 * first but not the second: the production container is a bare JRE with no `~/.cache/scryfall`, so
 * computing the answer live would mean downloading 24 MB at boot to serve a badge on one page. That
 * is the same problem the coverage *denominator* has, and it gets the same answer — bake it into a
 * classpath resource next to `set-totals.json` and join at request time. `just assay-bake` writes it;
 * [com.wingedsheep.assay.bake.VerdictLedger] explains why the file is also this module's regression
 * ledger and therefore re-blessed by hand rather than by the build.
 *
 * ## Missing is "unknown", never "no"
 *
 * A fresh checkout that has not run `just assay-bake`, or a card the ledger has no row for, yields
 * `null` rather than a negative verdict, and the view renders nothing instead of a badge claiming
 * Assay cannot read a card it has never been asked about. The distinction is load-bearing in the
 * other direction too: "0 cards Assay-ready in this set" is a real, useful finding, and it must not
 * be indistinguishable from "nobody baked the ledger".
 */
@Service
class AssayVerdictService {

    /**
     * One card's reading. [readsWhole] is the badge; [kind] and [line] are the tooltip — the decline
     * that stopped it and the printed line that decline points at, so a "no" says why.
     */
    data class Verdict(
        val readsWhole: Boolean,
        /** `LINE_DECLINED`, `MULTI_FACE`, `HEADER`, … — null when [readsWhole]. */
        val kind: String?,
        /** The blocking printed line, truncated by the bake. Null when the decline is card-wide. */
        val line: String?,
    )

    @Serializable
    private data class Row(val name: String, val kind: String? = null, val line: String? = null)

    @Serializable
    private data class Ledger(val corpus: Int = 0, val whole: Int = 0, val cards: List<Row> = emptyList())

    private val byName: Map<String, Verdict> = load()

    /** Whether a ledger was found at all. Lets a caller report "unknown" rather than "none ready". */
    val available: Boolean get() = byName.isNotEmpty()

    /**
     * This card's reading, or null if the ledger has no row for it.
     *
     * Looked up by front-face name, matching how `SetCoverageService` keys everything else: the
     * baked canonical lists and the ledger both spell a double-faced card `"Front // Back"`, but the
     * two are generated from different Scryfall products and only the front face is reliably equal.
     */
    fun verdict(name: String): Verdict? = byName[frontFace(name)]

    private fun load(): Map<String, Verdict> {
        val resource = ClassPathResource(RESOURCE_PATH)
        if (!resource.exists()) {
            log.info(
                "No {} on the classpath — Assay coverage badges are off. Run `just assay-bake` to add them.",
                RESOURCE_PATH,
            )
            return emptyMap()
        }
        return try {
            val ledger = resource.inputStream.bufferedReader().use {
                JSON.decodeFromString<Ledger>(it.readText())
            }
            log.info("Assay verdicts: {} of {} corpus cards read whole", ledger.whole, ledger.corpus)
            // Last row wins on a duplicate front-face name (a card printed under two full names that
            // share a front face). They carry the same reading, so which one survives cannot matter;
            // what would matter is throwing here and taking the whole coverage page down with it.
            ledger.cards.associate { frontFace(it.name) to Verdict(it.kind == null, it.kind, it.line) }
        } catch (e: Exception) {
            // A malformed ledger degrades to no badges rather than a server that will not boot. The
            // page it feeds is informational, and `just assay-bake` fails loudly at the point the
            // file is actually produced.
            log.warn("Could not read {}: {} — Assay coverage badges are off", RESOURCE_PATH, e.toString())
            emptyMap()
        }
    }

    private companion object {
        const val RESOURCE_PATH = "coverage/assay-verdicts.json"
        val JSON = Json { ignoreUnknownKeys = true }
        val log = LoggerFactory.getLogger(AssayVerdictService::class.java)

        /** Strip a ` // back` suffix, mirroring `SetCoverageService.frontFace`. */
        fun frontFace(name: String): String = name.substringBefore(" // ").trim()
    }
}
