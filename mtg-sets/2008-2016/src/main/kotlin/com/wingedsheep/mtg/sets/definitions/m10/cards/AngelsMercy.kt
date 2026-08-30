package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Angel's Mercy
 * {2}{W}{W}
 * Instant
 *
 * You gain 7 life.
 */
val AngelsMercy = card("Angel's Mercy") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "You gain 7 life."

    spell {
        effect = Effects.GainLife(7)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Andrew Robinson"
        flavorText = "\"Until that day I cursed the angels, those uncaring lights in the sky. They rewarded my scorn with the gift of time, every year of which I've spent in devotion to their names.\"\n—Torian Sha, soul warden"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b911124-3646-4014-b574-13fee44bfad5.jpg?1783942405"
    }
}
