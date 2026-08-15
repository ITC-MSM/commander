package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Silent Artisan
 * {3}{W}{W}
 * Creature — Giant
 * 3/5
 *
 * Vanilla — no rules text.
 */
val SilentArtisan = card("Silent Artisan") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant"
    power = 3
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Anthony Palumbo"
        flavorText = "On the fourth day they passed through a forest of immense stacked stones. Althemone, youngest of the companions, called these pillars the work of a god, but the Champion knew better. She quickened her pace.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dce5647d-1546-4eff-a2a2-9e9ef26db533.jpg?1783939806"
    }
}
