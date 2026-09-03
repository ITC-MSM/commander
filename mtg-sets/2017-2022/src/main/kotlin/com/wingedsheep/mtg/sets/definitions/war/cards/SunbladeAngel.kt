package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sunblade Angel
 * {5}{W}
 * Creature — Angel
 * 3/3
 * Flying, first strike, vigilance, lifelink
 */
val SunbladeAngel = card("Sunblade Angel") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flying, first strike, vigilance, lifelink"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE, Keyword.VIGILANCE, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "31"
        artist = "Johannes Voss"
        flavorText = "At Feather's command, squadrons of battle angels swarmed out of the *Parhelion II*, unsheathing blades made of molten dawn."
        imageUri = "https://cards.scryfall.io/normal/front/9/2/9211c0e1-b314-495f-9c43-9a0e95c9efb9.jpg"
    }
}
