package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Benthic Behemoth
 * {5}{U}{U}{U}
 * Creature — Serpent
 * 7/6
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 */
val BenthicBehemoth = card("Benthic Behemoth") {
    manaCost = "{5}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    power = 7
    toughness = 6
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls an Island.)"

    keywords(Keyword.ISLANDWALK)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "54"
        artist = "Jim Nelson"
        flavorText = "\"Deep devourer concealed in darkhome\n" +
            "Shrouded it seeks all becomes fodder\n" +
            "Once we swam alone and asea\n" +
            "But no more.\"\n" +
            "—*Rootwater Saga*"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc9fb7b6-d20c-4c08-9dae-4ccc9138b662.jpg"
    }
}
