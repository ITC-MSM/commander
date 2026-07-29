package com.wingedsheep.gameserver.ai

import com.wingedsheep.ai.engine.SealedDeckGenerator
import com.wingedsheep.ai.engine.deck.ConstructedDeckGenerator
import com.wingedsheep.gameserver.lobby.AiDeckSpec
import com.wingedsheep.sdk.core.DeckFormat
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Builds the decklist for any quick-lobby seat that didn't bring one — the AI seat's [AiDeckSpec],
 * and a human seat that picked "Random".
 *
 * Both seats go through here so the two axes — *what the seat asked for* and *what the lobby's
 * format allows* — are crossed in exactly one place. They used to be crossed in two: the AI seat
 * honoured the lobby format while a human on Random always got a 40-card sealed pool, so a Pauper
 * lobby could seat a rare-filled sealed deck opposite a legal 60-card Pauper deck. The matrix:
 *
 * |                | no format / limited lobby   | constructed format (Standard, Pauper, …) |
 * |----------------|-----------------------------|------------------------------------------|
 * | AI [AiDeckSpec.Auto]  | sealed pool, human's set | 60-card legal deck, whole card base |
 * | AI [AiDeckSpec.Sets]  | sealed pool, chosen sets | 60-card legal deck, chosen sets     |
 * | AI [AiDeckSpec.Fixed] | the submitted list       | the submitted list (validated on submit) |
 * | human "Random"        | sealed pool, their set   | 60-card legal deck, whole card base |
 *
 * A seat's *set* choice is a limited-pool concept: it says which boosters to open. Under a
 * constructed format the format defines the pool instead, so the set choice drops out — for the
 * human exactly as it already did for the AI's Auto.
 *
 * **Commander-shape formats are a known gap.** Commander / Brawl / Standard Brawl need a designated
 * commander in the command zone, singleton construction and a colour-identity constraint over the
 * whole deck — none of which the builders model. Rather than ship an illegal 100-card approximation,
 * those lobbies fall through to the limited path and the client says so on the AI panel. A host who
 * wants a real Commander opponent can still pick [AiDeckSpec.Fixed] and hand the AI a real
 * Commander decklist.
 */
@Component
class RandomDeckResolver(
    private val sealedDeckGenerator: SealedDeckGenerator,
    private val constructedDeckGenerator: ConstructedDeckGenerator,
) {
    private val logger = LoggerFactory.getLogger(RandomDeckResolver::class.java)

    /**
     * The AI seat's deck.
     *
     * @param spec what the host chose for the AI seat.
     * @param format the lobby's deck-format restriction, or null for none.
     * @param fallbackSetCode the set the lobby already resolved for its random pools — used by
     *        [AiDeckSpec.Auto] on the limited path so the AI and the human open the same set.
     */
    fun resolve(spec: AiDeckSpec, format: DeckFormat?, fallbackSetCode: String): Map<String, Int> {
        // A fixed list is the host's explicit answer and was validated against the format when it
        // was submitted; nothing left to decide.
        if (spec is AiDeckSpec.Fixed) return spec.deckList

        // An empty set selection means the host cleared the picker rather than that they want an
        // empty pool — treat it as Auto instead of failing the game start.
        val setCodes = (spec as? AiDeckSpec.Sets)?.setCodes?.filter { it.isNotBlank() }.orEmpty()

        return randomDeck(format, setCodes, fallbackSetCode)
    }

    /**
     * A generated deck for a seat with no submitted list, honouring the lobby's [format].
     *
     * @param format the lobby's deck-format restriction, or null for none.
     * @param setCodes sets the seat pinned its pool to; empty means "whatever the lobby resolved",
     *        i.e. [fallbackSetCode] on the limited path and the whole legal card base on the
     *        constructed one.
     * @param fallbackSetCode the single set to open boosters from when [setCodes] is empty.
     */
    fun randomDeck(format: DeckFormat?, setCodes: List<String>, fallbackSetCode: String): Map<String, Int> {
        if (format != null && !format.isCommanderShape) {
            // Constructed lobby: build to the format so both sides of the table play under the
            // same restriction. Falls back to the limited path if the legal pool is unusably thin
            // (a narrow format crossed with a narrow set selection) — a limited deck the seat can
            // actually play beats failing the game start.
            val built = runCatching { constructedDeckGenerator.generate(setCodes, format) }
                .onFailure { error ->
                    logger.warn(
                        "Constructed deck for {} ({}) failed; falling back to a sealed pool",
                        format.displayName,
                        if (setCodes.isEmpty()) "all sets" else setCodes.joinToString(", "),
                        error,
                    )
                }
                .getOrNull()
            if (built != null) return built
        } else if (format != null) {
            logger.info(
                "Lobby format {} is commander-shaped; the seat falls back to a limited deck",
                format.displayName,
            )
        }

        return if (setCodes.isEmpty()) {
            sealedDeckGenerator.generate(fallbackSetCode)
        } else {
            sealedDeckGenerator.generate(setCodes)
        }
    }
}
