package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gutter Skulk
 * {1}{B}
 * Creature — Zombie Rat
 * 2/2
 *
 * Vanilla — no rules text.
 */
val GutterSkulk = card("Gutter Skulk") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Rat"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Mark Winters"
        flavorText = "Upon finding his warehouse infested, Gaven didn't know whether to get an exterminator or an exorcist."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/830c7c77-20c4-429f-88c7-b85ab7a0e38b.jpg?1783940131"
    }
}
