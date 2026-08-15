package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity


/**
 * Iron Giant
 * {7}
 * Artifact Creature — Demon
 * 6/6
 * Vigilance, reach, trample
 */
val IronGiant = card("Iron Giant") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Demon"
    oracleText = "Vigilance, reach, trample"
    power = 6
    toughness = 6
    keywords(Keyword.VIGILANCE, Keyword.REACH, Keyword.TRAMPLE)
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "260"
        artist = "John Tyler Christopher"
        flavorText = "The iron giant is both hostile and powerful, using a giant blade to fell its foes. The mere mention of its name strikes fear in the hearts of drivers who know how perilous the roads are at night."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e48cf6d5-4d32-4b66-80be-3495ecd3e906.jpg"
    }
}
