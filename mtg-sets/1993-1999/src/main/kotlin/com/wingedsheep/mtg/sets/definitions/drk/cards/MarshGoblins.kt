package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Marsh Goblins
 * {B}{R}
 * Creature — Goblin
 * 1/1
 * Swampwalk
 */
val MarshGoblins = card("Marsh Goblins") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Goblin"
    power = 1
    toughness = 1
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"

    keywords(Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Quinton Hoover"
        flavorText = "Even the other Goblin races shun the Marsh Goblins, thanks to certain unwholesome customs they practice."
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8aabd80f-a18a-4bc1-9f05-4c3a63de77ce.jpg?1783947928"
    }
}
