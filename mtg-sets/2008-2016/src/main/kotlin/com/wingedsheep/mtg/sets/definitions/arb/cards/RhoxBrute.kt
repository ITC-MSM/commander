package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rhox Brute
 * {2}{R}{G}
 * Creature — Rhino Warrior
 * 4/4
 *
 * Vanilla — no rules text.
 */
val RhoxBrute = card("Rhox Brute") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Rhino Warrior"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Raymond Swanland"
        flavorText = "\"In this new world, I have seen the once-devout rhoxes take up arms with the savages of Jund. I would be offended, but I see the wisdom in their choice.\"\n—Gernan, Dawnray archer"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/237de286-5bf0-4c8e-8504-2d01d3133b55.jpg?1783942429"
    }
}
