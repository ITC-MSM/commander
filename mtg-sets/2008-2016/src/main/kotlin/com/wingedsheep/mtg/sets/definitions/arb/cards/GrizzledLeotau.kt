package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grizzled Leotau
 * {G}{W}
 * Creature — Cat
 * 1/5
 *
 * Vanilla — no rules text.
 */
val GrizzledLeotau = card("Grizzled Leotau") {
    manaCost = "{G}{W}"
    colorIdentity = "WG"
    typeLine = "Creature — Cat"
    power = 1
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Lars Grant-West"
        flavorText = "\"There is no glory in a death of age, as even the leotau know. As winter steals into their coats, they seek the deadliest lands, that they may die as they lived.\"\n—Aarsil the Blessed"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b388381-9e13-4ce7-b5b3-56a74cc23d93.jpg?1783942427"
    }
}
