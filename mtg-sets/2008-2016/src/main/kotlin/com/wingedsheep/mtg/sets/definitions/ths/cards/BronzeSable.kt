package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bronze Sable
 * {2}
 * Artifact Creature — Sable
 * 2/1
 *
 * Vanilla — no rules text.
 */
val BronzeSable = card("Bronze Sable") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Sable"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "212"
        artist = "Jasper Sandner"
        flavorText = "The Champion stood alone between the horde of the Returned and the shrine to Karametra, cutting down scores among hundreds. She would have been overcome if not for the aid of the temple guardians whom Karametra awakened.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba7e563d-964c-4afd-9e21-9d400f8719d4.jpg?1783939721"
    }
}
