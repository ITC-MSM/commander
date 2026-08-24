package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Volcanic Upheaval
 * {3}{R}
 * Instant
 * Destroy target land.
 */
val VolcanicUpheaval = card("Volcanic Upheaval") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Destroy target land."

    spell {
        val land = target("target land", Targets.Land)
        effect = Effects.Destroy(land)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "161"
        artist = "Yeong-Hao Han"
        flavorText = "Like a living organism, Zendikar rids itself of infection, and it does so abruptly and " +
            "ruthlessly."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d90bd68-0521-4db3-b590-a4e007da9f2e.jpg?1783938191"
    }
}
