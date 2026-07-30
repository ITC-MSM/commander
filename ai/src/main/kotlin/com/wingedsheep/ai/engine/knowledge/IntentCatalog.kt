package com.wingedsheep.ai.engine.knowledge

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.model.CardDefinition

/**
 * Card-name → [CardIntent] lookup, and the switch that turns Phase 6 on.
 *
 * The AI only ever holds a card *name* (`CardComponent.name`) — resolving that to the
 * [CardDefinition] the analyzer needs takes a [CardRegistry], which only `AIPlayer.create` has. So
 * the catalog is built there, threaded into the consumers, and carried by an
 * [com.wingedsheep.ai.engine.AiProfile] flag.
 *
 * [NONE] is the off position and the default everywhere. It answers `null` to every question, and
 * every consumer falls back to exactly what it did before Phase 6 — which is what keeps
 * `AiProfile.LEGACY_V0`, the permanent reference opponent every published arena number is quoted
 * against, byte-for-byte frozen.
 *
 * Analysis itself is memoized process-wide in [CardIntentAnalyzer], so a catalog is a thin handle:
 * building one per seat per game costs nothing.
 */
class IntentCatalog private constructor(private val registry: CardRegistry?) {

    /** Whether this catalog can answer anything at all. False for [NONE]. */
    val isEnabled: Boolean get() = registry != null

    /**
     * The intent of the card called [name], or null when the catalog is off or the name is not a
     * real card (a token, or a card from a set this registry never loaded).
     *
     * Callers must treat null as "no information" and keep their pre-Phase-6 behaviour, not as
     * "this card does nothing".
     */
    fun forName(name: String): CardIntent? {
        val definition = registry?.getCard(name) ?: return null
        return CardIntentAnalyzer.analyze(definition)
    }

    /** The intent of a definition already in hand. Always answers, even on [NONE]. */
    fun forCard(card: CardDefinition): CardIntent = CardIntentAnalyzer.analyze(card)

    companion object {
        /** The off position: no registry, no answers, pre-Phase-6 behaviour everywhere. */
        val NONE = IntentCatalog(null)

        /** A catalog backed by [registry]. */
        fun of(registry: CardRegistry): IntentCatalog = IntentCatalog(registry)
    }
}
