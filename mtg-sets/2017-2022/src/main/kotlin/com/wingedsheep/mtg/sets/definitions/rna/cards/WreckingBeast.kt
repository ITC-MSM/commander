package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.riot
import com.wingedsheep.sdk.model.Rarity

/**
 * Wrecking Beast — Ravnica Allegiance #150
 * {5}{G}{G} · Creature — Beast · 6 / 6
 *
 * Riot plus printed trample. [riot] already adds `Keyword.RIOT` to the keyword set, so only
 * trample is listed here.
 */
val WreckingBeast = card("Wrecking Beast") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 6
    toughness = 6
    oracleText = "Riot (This creature enters with your choice of a +1/+1 counter or haste.)\n" +
        "Trample"

    riot()
    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Izzy"
        flavorText = "\"The best construction makes the most satisfying destruction.\"\n" +
        "—Domri Rade"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74e6f7be-4493-4081-ac67-d782ab2b3723.jpg"
    }
}
