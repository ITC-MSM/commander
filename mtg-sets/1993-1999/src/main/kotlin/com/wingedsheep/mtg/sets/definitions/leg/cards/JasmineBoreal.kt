package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jasmine Boreal
 * {3}{G}{W}
 * Legendary Creature — Human
 * 4/5
 *
 * Vanilla — no rules text.
 */
val JasmineBoreal = card("Jasmine Boreal") {
    manaCost = "{3}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Legendary Creature — Human"
    power = 4
    toughness = 5

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Richard Kane Ferguson"
        flavorText = "\"Peace must prevail, even if the wicked must die.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db6ef678-4ce9-48d6-aa4f-2afd9a1ad724.jpg?1783948038"
    }
}
