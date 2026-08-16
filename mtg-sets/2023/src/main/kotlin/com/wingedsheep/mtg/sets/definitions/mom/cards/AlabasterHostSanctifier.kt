package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alabaster Host Sanctifier
 * {1}{W}
 * Creature — Phyrexian Cleric
 * 2/2
 * Lifelink
 */
val AlabasterHostSanctifier = card("Alabaster Host Sanctifier") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Phyrexian Cleric"
    oracleText = "Lifelink"
    power = 2
    toughness = 2

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Konstantin Porubov"
        flavorText = "\"Heliod shines his light on all things, excising the shadows of doubt. " +
            "Rejoice, for beneath his purifying eyes, Theros is united as one!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efbd934a-39c4-4ce7-af2a-34ca226d7f23.jpg?1783917073"
    }
}
