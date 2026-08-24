package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pale Bears
 * {2}{G}
 * Creature — Bear
 * 2/2
 *
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 *
 * Islandwalk alone — the same `BlockEvasionRules` table entry as Moor Fiend's swampwalk.
 */
val PaleBears = card("Pale Bears") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Bear"
    power = 2
    toughness = 2
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls an Island.)"

    keywords(Keyword.ISLANDWALK)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "256"
        artist = "Anthony S. Waters"
        flavorText = "\"Daughter, on the day you have killed your Pale Bear, then will I give you your true name.\"\n—Lovisa Coldeyes, Balduvian Chieftain"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f19c2a3-6403-4a78-bf45-6e339578d673.jpg"
    }
}
