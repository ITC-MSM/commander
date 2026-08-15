package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Devilthorn Fox
 * {1}{W}
 * Creature — Fox
 * 3/1
 *
 * Vanilla — no rules text.
 */
val DevilthornFox = card("Devilthorn Fox") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Fox"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Filip Burburan"
        flavorText = "On expeditions through Ashmouth, the hunters of Devilthorn Lodge rely on the cleverness of foxes to counteract the mischief of devils."
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57bea4c2-7a15-4f31-938d-c4c906e4ebe7.jpg?1783937821"
    }
}
