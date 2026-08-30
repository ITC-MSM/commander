package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Glimpse the Future
 * {2}{U}
 * Sorcery
 * Look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard.
 */
val GlimpseTheFuture = card("Glimpse the Future") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard."

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(count = 3, keepCount = 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Andrew Robinson"
        flavorText = "\"No simple coin toss can solve this riddle. You must think and choose wisely.\"\n" +
            "—Shai Fusan, archmage"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4d875e9-713d-4ddb-ae0a-db8483366319.jpg"
    }
}
