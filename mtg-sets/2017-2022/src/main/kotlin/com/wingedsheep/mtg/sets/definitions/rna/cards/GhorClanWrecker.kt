package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.riot
import com.wingedsheep.sdk.model.Rarity

/**
 * Ghor-Clan Wrecker — Ravnica Allegiance #103
 * {3}{R} · Creature — Human Warrior · 2 / 2
 *
 * Riot plus printed menace.
 */
val GhorClanWrecker = card("Ghor-Clan Wrecker") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Riot (This creature enters with your choice of a +1/+1 counter or haste.)\n" +
        "Menace (This creature can't be blocked except by two or more creatures.)"

    riot()
    keywords(Keyword.MENACE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "David Palumbo"
        flavorText = "\"Today the Rubblebelt is a bit larger. That's a good day's work.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4da3969c-1979-4eee-828a-4a7189121eba.jpg"
    }
}
