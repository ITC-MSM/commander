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
    /**
     * The printed characteristics that are *not* text: power/toughness, a planeswalker's starting
     * loyalty, a battle's starting defense. Kept as the strings Scryfall prints rather than numbers,
     * because `*` and `1+*` are printed values the grammar has no model for and the reader must not
     * silently turn into a zero — [com.wingedsheep.assay.compile.CardCompiler] declines on them.
     *
     * Nothing in `grammar/` or `normalize/` reads these: they are card *header* fields, and the only
     * consumer is the compiler, which needs a whole `CardDefinition` rather than a whole reading.
     */
    val power: String? = null,
    val toughness: String? = null,
    val loyalty: String? = null,
    val defense: String? = null,
    /**
     * `image_uris.normal`, when the pasted object carries one. The only presentation field in the
     * corpus model, and it is here for one reason: the compiler's output is *played*, and a board of
     * art-less tiles is unreadable. No rule, gate or report reads it.
     */
    val imageUri: String? = null,
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
