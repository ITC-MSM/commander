package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dutiful Servants
 * {3}{W}
 * Creature — Zombie
 * 2/5
 *
 * Vanilla — no rules text.
 */
val DutifulServants = card("Dutiful Servants") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Volkan Baǵa"
        flavorText = "Buildings crumbled and monuments fell. The river bled and the sky wept tears of fire. All the while, servants silently continued their work, oblivious to it all."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/7684db4c-6eff-4da1-a410-48d707fb5bf1.jpg?1783936063"
    }
}
