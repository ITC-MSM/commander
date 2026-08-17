package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chrome Cat
 * {3}
 * Artifact Creature — Cat
 * 3 / 2
 * When this creature enters, scry 1.
 */
val ChromeCat = card("Chrome Cat") {
    manaCost = "{3}"
    typeLine = "Artifact Creature — Cat"
    oracleText = "When this creature enters, scry 1."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "236"
        artist = "Joe Slucher"
        flavorText = "\"I always say, 'If it's breathing, it's lying.' Luckily my friend here does neither.\"\n—Lord Xander"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85da50ba-2061-40f0-b3af-950b87f812cd.jpg?1783923062"
    }
}
