package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kitesail Scout
 * {W}
 * Creature — Kor Scout
 * 1/1
 * Flying
 */
val KitesailScout = card("Kitesail Scout") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Scout"
    power = 1
    toughness = 1
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Dan Murayama Scott"
        flavorText = "\"The wind in one's hair, the sun on one's back, the joy of open skies . . . the next " +
            "generation of kor must know this kind of peace.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68a07aad-4ed5-47ae-b04c-9b9919000f6c.jpg?1783938218"
    }
}
