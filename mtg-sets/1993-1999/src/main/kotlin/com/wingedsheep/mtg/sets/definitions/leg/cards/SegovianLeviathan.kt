package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Segovian Leviathan
 * {4}{U}
 * Creature — Leviathan
 * 3/3
 *
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 */
val SegovianLeviathan = card("Segovian Leviathan") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Leviathan"
    power = 3
    toughness = 3
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls an Island.)"

    keywords(Keyword.ISLANDWALK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "76"
        artist = "Melissa A. Benson"
        flavorText = "\"Leviathan, too! Can you catch him with a fish-hook/ or run a line round his tongue?\"\n" +
            "—*Job 40:25*"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e5a814f1-7f8d-4c2c-b706-ee0ed5892f7b.jpg?1783948071"
    }
}
