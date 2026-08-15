package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ordered Migration
 * {3}{W}{U}
 * Sorcery
 * Domain — Create a 1/1 blue Bird creature token with flying for each basic land type
 * among lands you control.
 */
val OrderedMigration = card("Ordered Migration") {
    manaCost = "{3}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Sorcery"
    oracleText = "Domain — Create a 1/1 blue Bird creature token with flying for each basic land type among lands you control."

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmounts.domain(),
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Bird"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/0/0/000d9280-a79a-4f9f-822c-7aaecbff3337.jpg?1712316187"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "258"
        artist = "Heather Hudson"
        flavorText = "\"Birds reach all parts of the world,\" said Barrin. \"They will make excellent scouts.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04d83a07-6054-45f1-bdf9-07f2006238d2.jpg?1562895903"
    }
}
