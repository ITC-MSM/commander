package com.wingedsheep.ai.engine.deck

import com.wingedsheep.engine.limited.BoosterGenerator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.core.DeckFormat
import com.wingedsheep.sdk.model.CardDefinition
import org.slf4j.LoggerFactory

/**
 * Builds a 60-card constructed deck that is legal in a given [DeckFormat].
 *
 * This is the constructed counterpart to [com.wingedsheep.ai.engine.SealedDeckGenerator]: where
 * that one opens boosters and autobuilds a 40-card limited deck, this one draws from a *constructed*
 * card pool — every card the format allows — and hands it to [RandomDeckGenerator] for the
 * two-colour, mana-curve-aware 60-card build (24 lands, up to 4 copies of a card).
 *
 * It exists because the AI seat used to play a limited deck no matter what the lobby asked for. A
 * Pauper lobby validates the human's deck down to commons and then sat them across from a sealed
 * pool full of rares; the format restriction only ever applied to one side of the table.
 *
 * **Per-card legality is the whole of the filter.** [CardDefinition.legalFormats] is Scryfall-sourced
 * and already accounts for bans and set legality, so "is this card allowed in Pauper" needs no
 * rarity check here. Structural rules on top of that (singleton, 100 cards, a commander in the
 * command zone) are *not* modelled: commander-shape formats are rejected outright rather than
 * approximated — see [generate].
 */
class ConstructedDeckGenerator(
    private val boosterGenerator: BoosterGenerator,
    private val cardRegistry: CardRegistry,
) {
    /**
     * Builds a format-legal deck from the cards printed in [setCodes].
     *
     * @param setCodes sets to draw from. Empty means "every set" — the full format-legal pool.
     * @param format the constructed format the deck must be legal in.
     * @throws IllegalArgumentException if [format] is commander-shaped (see class doc), or if the
     *         legal pool is too thin to build from.
     */
    fun generate(setCodes: List<String>, format: DeckFormat): Map<String, Int> {
        require(!format.isCommanderShape) {
            "Commander-shape formats need a designated commander and singleton rules; " +
                "ConstructedDeckGenerator only builds the 60-card constructed shape."
        }

        val pool = legalPool(setCodes, format)
        require(pool.isNotEmpty()) {
            "No ${format.displayName}-legal cards available" +
                if (setCodes.isEmpty()) "" else " in ${setCodes.joinToString(", ")}"
        }

        // Basic lands come from the chosen sets so the deck's art matches the pool it was built
        // from; an all-sets build falls back to whatever the generator has registered.
        val basics = if (setCodes.isEmpty()) {
            boosterGenerator.availableSets.values.firstOrNull()?.basicLands.orEmpty()
        } else {
            boosterGenerator.getBasicLands(setCodes).values.toList()
        }

        logger.info(
            "Building a {} deck from {} legal cards ({})",
            format.displayName,
            pool.size,
            if (setCodes.isEmpty()) "all sets" else setCodes.joinToString(", "),
        )
        return RandomDeckGenerator(
            cardPool = pool,
            basicLandVariants = basics,
            setCodes = setCodes,
        ).generate()
    }

    /** Convenience overload: build from the whole format-legal card base. */
    fun generate(format: DeckFormat): Map<String, Int> = generate(emptyList(), format)

    /**
     * Every card legal in [format], scoped to [setCodes] when non-empty.
     *
     * Set scoping reads [BoosterGenerator.SetConfig.cards] *and* resolves the set's reprint
     * [BoosterGenerator.SetConfig.printings] rows through the registry: a reprint's canonical
     * `CardDefinition` lives in its earliest printing's set, so a set that is mostly reprints
     * (a core set, a precon set) would otherwise look almost empty.
     */
    private fun legalPool(setCodes: List<String>, format: DeckFormat): List<CardDefinition> {
        val candidates = if (setCodes.isEmpty()) {
            cardRegistry.allCardNames().mapNotNull { cardRegistry.getCard(it) }
        } else {
            setCodes.flatMap { setCode ->
                val config = boosterGenerator.availableSets[setCode]
                    ?: throw IllegalArgumentException("Unknown set code: $setCode")
                config.cards + config.printings.mapNotNull { cardRegistry.getCard(it.name) }
            }
        }
        return candidates
            .distinctBy { it.name }
            // An empty `legalFormats` means we have no Scryfall legality data for the card at all
            // (custom//unreleased content). Excluding it keeps the deck honestly format-legal
            // rather than silently smuggling unknowns in.
            .filter { format in it.legalFormats }
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(ConstructedDeckGenerator::class.java)
    }
}
