package com.wingedsheep.assay.corpus

/**
 * One face's printed characteristics, straight off Scryfall. Single-faced cards have exactly one.
 *
 * Scryfall's `oracle_text` is already errata'd to current templating, which removes the biggest
 * obstacle to a round trip — thirty years of drifting wording — before Assay starts.
 */
data class OracleFace(
    val name: String,
    val oracleText: String,
    val typeLine: String = "",
    val manaCost: String = "",
)

/**
 * A card as the corpus serves it. Deliberately thin: Assay reads Oracle text, and the fields
 * beside it exist only to scope, name and classify a decline in the report.
 *
 * [scryfallKeywords] is Scryfall's own keyword tagging. The grammar never consults it — that would
 * be a second dictionary, which is the thing the design is getting rid of — but the gate uses it to
 * *scope* the Phase 1 acceptance number to vanilla and keyword-only cards.
 */
data class OracleCard(
    val name: String,
    val oracleId: String?,
    val layout: String,
    val setCode: String?,
    val scryfallKeywords: List<String>,
    val faces: List<OracleFace>,
) {
    /** True when no face has rules text at all — the vanilla quarter of the corpus. */
    val isVanilla: Boolean get() = faces.all { it.oracleText.isBlank() }
}
