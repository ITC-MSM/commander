package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rubble Slinger — Ravnica Allegiance #217
 * {2}{R/G} · Creature — Human Warrior · 2 / 3
 *
 * Vanilla reach.
 */
val RubbleSlinger = card("Rubble Slinger") {
    manaCost = "{2}{R/G}"
    colorIdentity = "GR"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 3
    oracleText = "Reach"

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "217"
        artist = "Livia Prima"
        flavorText = "\"Tear down the city, lie by lie. Then throw it back at the liars, stone by stone.\"\n" +
        "—Domri Rade"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f006255f-b18d-4d52-b97a-17909b67decc.jpg"
    }
}
