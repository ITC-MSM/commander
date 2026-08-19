package com.wingedsheep.mtg.sets.definitions.aer.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gifted Aetherborn
 * {B}{B}
 * Creature — Aetherborn Vampire
 * 2/3
 *
 * Deathtouch, lifelink
 */
val GiftedAetherborn = card("Gifted Aetherborn") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Aetherborn Vampire"
    oracleText = "Deathtouch, lifelink"
    power = 2
    toughness = 3

    keywords(Keyword.DEATHTOUCH, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "61"
        artist = "Ryan Yee"
        flavorText = "A few aetherborn have discovered a way to sustain their own existences at the cost of an insatiable hunger for the life essence of other beings."
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abceb4fd-e3c5-400d-af7a-6dd17108a4b4.jpg?1783936763"
    }
}
