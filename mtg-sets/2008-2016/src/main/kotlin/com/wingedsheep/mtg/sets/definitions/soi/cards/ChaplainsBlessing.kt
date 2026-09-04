package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chaplain's Blessing (Shadows over Innistrad #10)
 * {W}
 * Sorcery
 *
 * You gain 5 life.
 */
val ChaplainsBlessing = card("Chaplain's Blessing") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "You gain 5 life."

    spell {
        effect = Effects.GainLife(5)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "John Stanko"
        flavorText = "\"There was a time when the purpose of the church was to heal and protect. I would see that time return.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f70ea481-1751-4097-af41-2d13fe79e788.jpg?1783937824"
    }
}
