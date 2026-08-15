package com.wingedsheep.assay.corpus

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * One Scryfall card object → one [OracleCard].
 *
 * This is a *reader*, not a source: [OracleCorpus] streams the bulk file through it a line at a
 * time, and the compiler ([com.wingedsheep.assay.compile.CardCompiler]) runs a single pasted object
 * through the very same function. That sharing is the point — a pasted card must be read byte-for-
 * byte the way a corpus card is, or the compiler would be a second, unmeasured reader of Scryfall
 * JSON and the gates would no longer say anything about what it produces.
 *
 * A custom card with no Scryfall entry is the same shape by construction: it is Scryfall-*style*
 * JSON, and every field this reads is one an author can write by hand. Nothing here consults the
 * network or the corpus.
 */
object ScryfallJson {

    /**
     * Layouts that carry no Oracle text worth assaying — art cards, tokens the corpus mints
     * elsewhere, and the acorn-adjacent formats the design's non-goals rule out. Excluded from the
     * corpus rather than declined, because counting them as declines would make the fineness
     * denominator dishonest in our favour *and* against us at the same time.
     */
    private val EXCLUDED_LAYOUTS = setOf(
        "art_series", "token", "double_faced_token", "emblem", "vanguard", "scheme", "planar",
    )

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /**
     * Read one card object's JSON text. Returns null on malformed JSON as well as on an excluded
     * layout or a non-English printing — a reader on the corpus's hot path must not throw, and the
     * compiler turns the null into a named decline rather than a stack trace.
     */
    fun read(text: String): OracleCard? =
        runCatching { read(json.parseToJsonElement(text).jsonObject) }.getOrNull()

    fun read(obj: JsonObject): OracleCard? {
        val layout = obj.str("layout") ?: "normal"
        if (layout in EXCLUDED_LAYOUTS) return null
        if ((obj.str("lang") ?: "en") != "en") return null
        val name = obj.str("name") ?: return null

        val faces = (obj["card_faces"] as? JsonArray)
            ?.filterIsInstance<JsonObject>()
            ?.map { face -> face.toFace(fallbackName = name, card = obj) }
            ?: listOf(obj.toFace(fallbackName = name, card = obj))

        return OracleCard(
            name = name,
            oracleId = obj.str("oracle_id"),
            layout = layout,
            setCode = obj.str("set")?.uppercase(),
            scryfallKeywords = (obj["keywords"] as? JsonArray)?.mapNotNull { (it as? JsonPrimitive)?.content }
                ?: emptyList(),
            faces = faces,
        )
    }

    /**
     * A face's own fields, falling back to the card's where Scryfall puts a shared value at the top
     * level. `type_line` is the documented case (split cards print one); power/toughness/loyalty are
     * read face-first for the same reason, since a single-faced card *is* its own face here.
     */
    private fun JsonObject.toFace(fallbackName: String, card: JsonObject) = OracleFace(
        name = str("name") ?: fallbackName,
        oracleText = str("oracle_text") ?: "",
        typeLine = str("type_line") ?: card.str("type_line") ?: "",
        manaCost = str("mana_cost") ?: "",
        power = str("power") ?: card.str("power"),
        toughness = str("toughness") ?: card.str("toughness"),
        loyalty = str("loyalty") ?: card.str("loyalty"),
        defense = str("defense") ?: card.str("defense"),
        imageUri = imageUri() ?: card.imageUri(),
    )

    /** `image_uris.normal`, the size every other surface in the repo links. */
    private fun JsonObject.imageUri(): String? = (this["image_uris"] as? JsonObject)?.str("normal")

    private fun JsonObject.str(key: String): String? =
        (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content
}
