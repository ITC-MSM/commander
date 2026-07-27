package com.wingedsheep.engine.registry

import com.wingedsheep.engine.handlers.effects.token.TokenArt
import com.wingedsheep.mtg.sets.MtgSetCatalog
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.serialization.CardSerialization
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Corpus-wide gate: **every token any registered card can create resolves to an image.**
 *
 * A token entity with a null `imageUri` isn't a blank card in the client — it falls through to
 * `getScryfallFallbackUrl`, which asks `api.scryfall.com/cards/named?exact=<name>` and renders
 * whatever printing Scryfall happens to return. That is how Arahbo, the First Fang's Foundations
 * Cat ended up showing Dominaria Remastered art: nothing was *missing*, so nothing failed — the
 * art was just silently wrong. This test makes that state a build failure instead.
 *
 * It walks the serialised card tree rather than the effect objects, so a `CreateToken` nested
 * anywhere — inside a composite, a mode, a pipeline, a reflexive trigger, a granted ability, a
 * `ConvertCountersToTokens` — is still found. The resolution it checks is the executors'
 * ([com.wingedsheep.engine.handlers.effects.token.CreateTokenExecutor] and
 * [com.wingedsheep.engine.handlers.effects.token.CreatePredefinedTokenExecutor]):
 * explicit `imageUri` → the minting set's [TokenArtRegistry] entry → the generic
 * [TokenArt] table (or, for predefined tokens, the canonical printing in `PredefinedTokens`).
 *
 * Fixing a failure means adding the creature type to [TokenArt.IMAGES] — or, when the art has to
 * be exactly right for that set, adding a `TokenPrinting` to the set's `MtgSet.tokenArt`.
 */
class TokenArtCoverageTest : FunSpec({

    val tokenArtRegistry = TokenArtRegistry().apply {
        for (set in MtgSetCatalog.all) {
            register(set.code, set.tokenArt, set.cards.map { it.name })
        }
    }

    /** Art the predefined-token executor falls back to, keyed by `tokenType`. */
    val predefinedArt: Map<String, String?> =
        PredefinedTokens.allTokens.associate { it.name to it.metadata.imageUri }

    /** Every `CreateToken` / `CreatePredefinedToken` node anywhere in a card's serialised tree. */
    fun tokenNodes(card: CardDefinition): List<JsonObject> {
        val found = mutableListOf<JsonObject>()
        fun walk(element: JsonElement) {
            when (element) {
                is JsonObject -> {
                    val type = (element["type"] as? JsonPrimitive)?.contentOrNull
                    if (type == "CreateToken" || type == "CreatePredefinedToken") found += element
                    element.values.forEach(::walk)
                }
                is JsonArray -> element.forEach(::walk)
                else -> Unit
            }
        }
        walk(CardSerialization.json.encodeToJsonElement(CardDefinition.serializer(), card))
        return found
    }

    fun strings(node: JsonObject, field: String): List<String> =
        (node[field] as? JsonArray).orEmpty().mapNotNull { (it as? JsonPrimitive)?.contentOrNull }

    fun int(node: JsonObject, field: String): Int? =
        (node[field] as? JsonPrimitive)?.contentOrNull?.toIntOrNull()

    test("every token a card can create resolves to an image") {
        val gaps = mutableListOf<String>()

        for (set in MtgSetCatalog.all) {
            for (card in set.cards) {
                for (node in tokenNodes(card)) {
                    // An explicit per-card override always wins — nothing left to check.
                    if (node["imageUri"] != null) continue

                    val isPredefined =
                        (node["type"] as? JsonPrimitive)?.contentOrNull == "CreatePredefinedToken"

                    val resolved = if (isPredefined) {
                        val tokenType = node["tokenType"]?.jsonPrimitive?.contentOrNull ?: continue
                        tokenArtRegistry.resolve(card.name, tokenType) ?: predefinedArt[tokenType]
                    } else {
                        // A token whose creature type is chosen at resolution time (Riptide
                        // Replicator, "of the chosen type") has no statically-known type, so
                        // there is nothing to look up here; the executor falls back at runtime.
                        if (node["creatureTypesFromChoice"] != null) continue
                        val creatureTypes = strings(node, "creatureTypes")
                        val name = node["name"]?.jsonPrimitive?.contentOrNull
                            ?: creatureTypes.joinToString(" ")
                        tokenArtRegistry.resolve(
                            sourceCardDefinitionId = card.name,
                            tokenName = name,
                            power = int(node, "power"),
                            toughness = int(node, "toughness"),
                        ) ?: TokenArt.forCreatureTypes(creatureTypes)
                    }

                    if (resolved == null) {
                        val what = if (isPredefined) {
                            node["tokenType"]?.jsonPrimitive?.contentOrNull
                        } else {
                            strings(node, "creatureTypes").joinToString(" ").ifEmpty { "<no type>" }
                        }
                        gaps += "[${set.code}] ${card.name} -> $what"
                    }
                }
            }
        }

        if (gaps.isNotEmpty()) {
            val types = gaps.map { it.substringAfterLast("-> ") }.distinct().sorted()
            println("=== tokens with no art: ${gaps.size} across ${types.size} kinds ===")
            gaps.distinct().sorted().forEach { println("  $it") }
            println("--- add these to TokenArt.IMAGES (or the set's tokenArt) ---")
            types.forEach { println("  $it") }
        }
        gaps.shouldBeEmpty()
    }
})
