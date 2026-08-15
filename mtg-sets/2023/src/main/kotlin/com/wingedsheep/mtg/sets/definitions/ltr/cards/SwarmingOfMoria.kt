package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Swarming of Moria
 * {2}{R}
 * Sorcery
 *
 * Create a Treasure token.
 * Amass Orcs 2.
 */
val SwarmingOfMoria = card("Swarming of Moria") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")\n" +
        "Amass Orcs 2. (Put two +1/+1 counters on an Army you control. It's also an Orc. If you don't " +
        "control an Army, create a 0/0 black Orc Army creature token first.)"

    spell {
        effect = Effects.CreateTreasure()
            .then(Effects.Amass(2, "Orc"))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Pavel Kolomeyets"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a1bd073-4351-4c56-9b07-4430d0d83084.jpg?1686969195"
    }
}
