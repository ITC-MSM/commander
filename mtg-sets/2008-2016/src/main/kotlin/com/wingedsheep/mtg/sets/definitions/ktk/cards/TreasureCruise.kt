package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Treasure Cruise
 * {7}{U}
 * Sorcery
 * Delve
 * Draw three cards.
 */
val TreasureCruise = card("Treasure Cruise") {
    manaCost = "{7}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Delve (Each card you exile from your graveyard while casting this spell pays for {1}.)\nDraw three cards."

    keywords(Keyword.DELVE)

    spell {
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Cynthia Sheppard"
        flavorText = "Countless delights drift on the surface while dark schemes run below."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a59d4b1-6cf4-44ec-8a96-1bb7094fea21.jpg?1562788963"
    }
}
