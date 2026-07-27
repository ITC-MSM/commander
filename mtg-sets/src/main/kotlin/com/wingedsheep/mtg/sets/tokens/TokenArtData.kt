package com.wingedsheep.mtg.sets.tokens

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.model.TokenPrinting
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Bundled per-set token printings, synced from Scryfall's token sets (`t<code>`).
 *
 * This is the bulk layer behind `MtgSet.tokenArt`: rather than hand-authoring a row for every
 * token every set prints, `tokens.json` carries all of them and the wiring registers
 * `set.tokenArt + TokenArtData.forSet(set.code)` — hand-authored rows first, so a set that wants
 * to override the synced art (or supply art Scryfall has none of) simply declares it.
 *
 * Refresh with `./gradlew :mtg-sets:syncTokenArt` (see [SyncTokenArtKt]).
 *
 * ## What isn't in here
 * Roughly a third of the sets we implement are old enough that Wizards never printed token cards
 * for them — Alpha through Invasion, Tempest, Odyssey, Onslaught. Scryfall has no `t<code>` set to
 * sync, so their tokens fall through to the engine-wide generic art unless a set declares its own
 * (Invasion self-hosts Saproling and Reflection art under `web-client/public/images/tokens/`).
 */
object TokenArtData {

    private const val RESOURCE = "/tokens.json"

    /** Parsed once on first touch; empty if the resource is missing. */
    private val bySet: Map<String, List<TokenPrinting>> by lazy { load() }

    /** Token printings this set contributes, or empty when the set has none on Scryfall. */
    fun forSet(setCode: String): List<TokenPrinting> = bySet[setCode].orEmpty()

    /** Set codes carrying synced token art. */
    val setCodes: Set<String> get() = bySet.keys

    /** Total synced printings, for diagnostics. */
    val size: Int get() = bySet.values.sumOf { it.size }

    // ---------------------------------------------------------------------------------------
    // Wire format
    // ---------------------------------------------------------------------------------------

    /**
     * On-disk row. Separate from [TokenPrinting] so the SDK model stays free of serialization
     * concerns and the file can carry provenance ([scryfallId]) the engine doesn't need.
     *
     * [power] / [toughness] are null for noncreature tokens (Treasure, Clue, Role). [colors] is
     * an empty list for a colorless token — distinct from "unspecified", which this format has no
     * way to express and doesn't need: every synced row comes from a real printing.
     */
    @Serializable
    private data class Row(
        val name: String,
        val imageUri: String,
        val power: Int? = null,
        val toughness: Int? = null,
        val colors: List<String> = emptyList(),
        val scryfallId: String? = null,
    )

    private val parser = Json { ignoreUnknownKeys = true }
    private val serializer = MapSerializer(String.serializer(), ListSerializer(Row.serializer()))

    private fun load(): Map<String, List<TokenPrinting>> {
        val text = TokenArtData::class.java.getResource(RESOURCE)?.readText() ?: return emptyMap()
        return parser.decodeFromString(serializer, text).mapValues { (_, rows) ->
            rows.map { row ->
                TokenPrinting(
                    name = row.name,
                    imageUri = row.imageUri,
                    power = row.power,
                    toughness = row.toughness,
                    colors = row.colors.mapNotNullTo(mutableSetOf()) { c ->
                        runCatching { Color.valueOf(c) }.getOrNull()
                    },
                )
            }
        }
    }
}
