package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hulking Devil
 * {3}{R}
 * Creature — Devil
 * 5/2
 *
 * Vanilla — no rules text.
 */
val HulkingDevil = card("Hulking Devil") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Devil"
    power = 5
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "165"
        artist = "Joseph Meehan"
        flavorText = "Fear the patient devil, the one who is calm in the midst of chaos. Beware the silent devil, the one whose cackle does not mingle with the others."
        imageUri = "https://cards.scryfall.io/normal/front/0/3/031ecfc4-cc84-4f74-8eb1-3eaa234d8093.jpg?1783937750"
    }
}
