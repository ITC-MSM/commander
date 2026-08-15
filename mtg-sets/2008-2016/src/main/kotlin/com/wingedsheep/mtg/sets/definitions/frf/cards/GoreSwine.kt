package com.wingedsheep.mtg.sets.definitions.frf.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gore Swine
 * {2}{R}
 * Creature — Boar
 * 4/1
 *
 * Vanilla — no rules text.
 */
val GoreSwine = card("Gore Swine") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Boar"
    power = 4
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Jack Wang"
        flavorText = "\"The Mardu are like the gore swine. We are wild, hunt in packs, and rarely clean the blood from our blades.\"\n—Vallash, Mardu warrior"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31c36d53-1173-4a55-8fb8-63a624fde7de.jpg?1783938688"
    }
}
