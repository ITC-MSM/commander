package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Happy Hogan, Dauntless Driver
 * {R}
 * Legendary Creature — Human Pilot
 * 2/1
 *
 * Vanilla — no rules text.
 */
val HappyHoganDauntlessDriver = card("Happy Hogan, Dauntless Driver") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Pilot"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "545"
        artist = "Ivan Shavrin"
        flavorText = "\"Traffic is a little heavy this morning, Tony, but we'll get you there on time.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/1/4174e5fa-8e28-41c5-ba5c-4ec27b7cd4a3.jpg?1783903100"
    }
}
