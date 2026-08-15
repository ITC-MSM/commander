package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Headwater Sentries
 * {3}{U}
 * Creature — Merfolk Warrior
 * 2/5
 *
 * Vanilla — no rules text.
 */
val HeadwaterSentries = card("Headwater Sentries") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Warrior"
    power = 2
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Naomi Baker"
        flavorText = "\"The elders say that if the intruders discovered the secret of the golden city, it would mean an end to our people.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2af2c338-f5e9-4596-9435-c6aa965ae541.jpg?1783935781"
    }
}
