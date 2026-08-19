package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Goldmeadow Dodger
 * {W}
 * Creature — Kithkin Rogue
 * 1/1
 * This creature can't be blocked by creatures with power 4 or greater.
 */
val GoldmeadowDodger = card("Goldmeadow Dodger") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Rogue"
    power = 1
    toughness = 1
    oracleText = "This creature can't be blocked by creatures with power 4 or greater."

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtLeast(4))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Omar Rayyan"
        flavorText = "\"I've gotten close enough to a giant to smell his breath, but none has ever so much as spotted me. I wonder how long my record can extend?\""
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5305b5c6-2af6-4b5c-9a57-0a2d2628e2f4.jpg?1783942915"
    }
}
