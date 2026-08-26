package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seraph of the Suns
 * {5}{W}{W}
 * Creature — Angel
 * 4/4
 * Flying
 * Indestructible (Damage and effects that say "destroy" don't destroy this creature. If its
 * toughness is 0 or less, it still dies.)
 */
val SeraphOfTheSuns = card("Seraph of the Suns") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flying\n" +
            "Indestructible (Damage and effects that say \"destroy\" don't destroy this creature. If its toughness is 0 or less, it still dies.)"
    power = 4
    toughness = 4

    keywords(Keyword.FLYING, Keyword.INDESTRUCTIBLE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Winona Nelson"
        flavorText = "\"Angels? My feelings remain unchanged.\"\n—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85dc4ed8-4674-44a1-8a06-ecce72c85e60.jpg?1783941503"
    }
}
