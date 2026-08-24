package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hired Poisoner
 * {B}
 * Creature — Human Assassin
 * 1/1
 * Deathtouch
 */
val HiredPoisoner = card("Hired Poisoner") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Assassin"
    oracleText = "Deathtouch"
    power = 1
    toughness = 1

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Joe Slucher"
        flavorText = "\"They don't even feel the cut. I'm ordering a drink in a nearby tavern before anyone notices something's wrong.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf97e572-90d6-46fc-81c3-956a7ef88983.jpg?1783934175"
    }
}
