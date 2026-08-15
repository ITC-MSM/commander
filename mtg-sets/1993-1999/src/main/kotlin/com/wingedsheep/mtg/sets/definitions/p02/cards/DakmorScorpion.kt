package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dakmor Scorpion
 * {1}{B}
 * Creature — Scorpion
 * 2/1
 *
 * Vanilla — no rules text.
 */
val DakmorScorpion = card("Dakmor Scorpion") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Scorpion"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Randy Gallegos"
        flavorText = "A scorpion this big you won't find curled up in your boot. Maybe *around* your boot, but not *in* it."
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d860dee-93a9-48e5-ba7a-80ad8cdc84e4.jpg?1783946477"
    }
}
