package com.wingedsheep.engine.limited

import com.wingedsheep.sdk.model.CardDefinition
import kotlin.random.Random

/**
 * Stateful, deterministic dealer for a cube.
 *
 * The complete cube is shuffled once and then consumed in order. This is deliberately separate
 * from [com.wingedsheep.sdk.limited.BoosterStrategy], whose per-pack API cannot preserve
 * no-replacement state across a draft.
 */
class CubeDealer(
    cube: List<CardDefinition>,
    private val packSize: Int,
    seed: Long,
) {
    init {
        require(packSize > 0) { "Cube pack size must be positive, got $packSize" }
    }

    private val shuffledCube = cube.shuffled(Random(seed))
    private var dealt = 0

    val remaining: Int
        get() = shuffledCube.size - dealt

    /**
     * Deal [packs] complete packs and consume them from this dealer.
     *
     * @throws IllegalArgumentException when [packs] is negative or the cube lacks enough cards.
     */
    fun deal(packs: Int): List<List<CardDefinition>> {
        require(packs >= 0) { "Number of cube packs must not be negative, got $packs" }
        val requestedCards = Math.multiplyExact(packs, packSize)
        require(requestedCards <= remaining) {
            val shortfall = requestedCards - remaining
            "Cannot deal $packs cube packs of $packSize cards: " +
                "$remaining cards remain, short by $shortfall"
        }

        val result = shuffledCube
            .subList(dealt, dealt + requestedCards)
            .chunked(packSize)
        dealt += requestedCards
        return result
    }
}
