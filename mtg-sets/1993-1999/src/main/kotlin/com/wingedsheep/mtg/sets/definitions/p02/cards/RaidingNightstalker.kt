package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Raiding Nightstalker
 * {2}{B}
 * Creature — Nightstalker
 * 2/2
 * Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)
 */
val RaidingNightstalker = card("Raiding Nightstalker") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightstalker"
    oracleText = "Swampwalk (This creature can't be blocked as long as defending player controls a Swamp.)"
    power = 2
    toughness = 2
    keywords(Keyword.SWAMPWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Pete Venters"
        flavorText = "\"Our steeds need food, water, stabling. Theirs just need prey.\"\n—Restela, Alaborn marshal"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0df2887c-e70b-4ff3-a437-450c0037fb07.jpg"
    }
}
