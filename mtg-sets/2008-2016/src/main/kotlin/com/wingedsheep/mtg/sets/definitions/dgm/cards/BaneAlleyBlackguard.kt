package com.wingedsheep.mtg.sets.definitions.dgm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bane Alley Blackguard
 * {1}{B}
 * Creature — Human Rogue
 * 1/3
 *
 * Vanilla — no rules text.
 */
val BaneAlleyBlackguard = card("Bane Alley Blackguard") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    power = 1
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Mike Bierek"
        flavorText = "\"I'm in the field of procurement, and business is good. The guilds want all kinds of maps and relics these days, though what they want them for I'm not quite sure.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/5/15fcad03-4567-4f96-976e-01a07d8ab050.jpg?1783940040"
    }
}
