package com.wingedsheep.gameserver.ai

import com.wingedsheep.ai.engine.SealedDeckGenerator
import com.wingedsheep.ai.engine.deck.ConstructedDeckGenerator
import com.wingedsheep.gameserver.lobby.AiDeckSpec
import com.wingedsheep.sdk.core.DeckFormat
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Turns the host's [AiDeckSpec] into the decklist the AI seat actually plays.
 *
 * This is the one place that knows how the two axes interact — *what the host asked for* and
 * *what the lobby's format allows* — so neither the lobby handler nor
 * [AiGameManager] has to. The matrix it implements:
 *
 * |                | no format / limited lobby   | constructed format (Standard, Pauper, …) |
 * |----------------|-----------------------------|------------------------------------------|
 * | [AiDeckSpec.Auto]  | sealed pool, human's set | 60-card legal deck, whole card base      |
 * | [AiDeckSpec.Sets]  | sealed pool, chosen sets | 60-card legal deck, chosen sets          |
 * | [AiDeckSpec.Fixed] | the submitted list       | the submitted list (validated on submit) |
 *
 * **Commander-shape formats are a known gap.** Commander / Brawl / Standard Brawl need a
 * designated commander in the command zone, singleton construction and a colour-identity
 * constraint over the whole deck — none of which the heuristic builders model. Rather than ship an
 * illegal 100-card approximation, those lobbies fall through to the limited path and the client
 * says so on the AI panel. A host who wants a real Commander opponent can still pick [AiDeckSpec.Fixed]
 * and hand the AI a real Commander decklist.
 */
@Component
class AiDeckResolver(
    private val sealedDeckGenerator: SealedDeckGenerator,
    private val constructedDeckGenerator: ConstructedDeckGenerator,
) {
    private val logger = LoggerFactory.getLogger(AiDeckResolver::class.java)

    /**
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

        if (format != null && !format.isCommanderShape) {
            // Constructed lobby: build to the format so both sides of the table play under the
            // same restriction. Falls back to the limited path if the legal pool is unusably thin
            // (a narrow format crossed with a narrow set selection) — a limited deck the AI can
            // actually play beats failing the game start.
            val built = runCatching { constructedDeckGenerator.generate(setCodes, format) }
                .onFailure { error ->
                    logger.warn(
                        "Constructed AI deck for {} ({}) failed; falling back to a sealed pool",
                        format.displayName,
                        if (setCodes.isEmpty()) "all sets" else setCodes.joinToString(", "),
                        error,
                    )
                }
                .getOrNull()
            if (built != null) return built
        } else if (format != null) {
            logger.info(
                "Lobby format {} is commander-shaped; the AI seat falls back to a limited deck",
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
