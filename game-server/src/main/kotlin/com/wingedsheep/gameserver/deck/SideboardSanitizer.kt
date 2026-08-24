package com.wingedsheep.gameserver.deck

import com.wingedsheep.engine.registry.CardRegistry

/**
 * Drops sideboard entries the card registry doesn't know, at the server boundary.
 *
 * A submitted *deck* is validated and rejected outright when it names a card this engine hasn't
 * implemented. A sideboard isn't: it lives "outside the game" (CR 100.4a) and reaches
 * `GameInitializer` unchecked, where `cardRegistry.requireCard` throws on the first unknown name —
 * so a single unimplemented sideboard card would fail game *initialization*, taking down a lobby
 * whose decks were all perfectly legal.
 *
 * Rejecting the submission instead would be worse: real decklists are pasted from Arena or
 * Moxfield with full 15-card sideboards, and one card the corpus hasn't reached yet would lock the
 * player out of a game they could otherwise play. So unknown names are dropped and reported to the
 * caller for logging — a wish simply won't find them, which is the truth of it.
 *
 * Non-positive counts are dropped too; they can't produce a card and would otherwise sit in the
 * lobby state as noise.
 */
object SideboardSanitizer {

    /**
     * @param kept entries the registry can resolve, safe to hand to the engine
     * @param dropped names that were discarded, for the caller's log line
     */
    data class Result(val kept: Map<String, Int>, val dropped: List<String>) {
        val hasDrops: Boolean get() = dropped.isNotEmpty()
    }

    fun sanitize(sideboard: Map<String, Int>, registry: CardRegistry): Result {
        if (sideboard.isEmpty()) return Result(emptyMap(), emptyList())
        val kept = LinkedHashMap<String, Int>(sideboard.size)
        val dropped = mutableListOf<String>()
        for ((name, count) in sideboard) {
            when {
                count <= 0 -> Unit
                registry.hasCard(name) -> kept[name] = count
                else -> dropped += name
            }
        }
        return Result(kept, dropped)
    }
}
