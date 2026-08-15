package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sir Shandlar of Eberyn
 * {4}{G}{W}
 * Legendary Creature — Human Knight
 * 4/7
 *
 * Vanilla — no rules text.
 */
val SirShandlarOfEberyn = card("Sir Shandlar of Eberyn") {
    manaCost = "{4}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Legendary Creature — Human Knight"
    power = 4
    toughness = 7

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "257"
        artist = "Andi Rusu"
        flavorText = "\"Remember Sir Shandlar! Remember and stand firm!\" —rallying cry of the Eberyn militia"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31570ded-f5e3-44c4-b95f-294ac10b2cd2.jpg?1783948033"
    }
}
