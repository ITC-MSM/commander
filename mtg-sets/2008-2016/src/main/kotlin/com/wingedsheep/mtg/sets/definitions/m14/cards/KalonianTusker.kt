package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kalonian Tusker
 * {G}{G}
 * Creature — Beast
 * 3/3
 *
 * Vanilla — no rules text.
 */
val KalonianTusker = card("Kalonian Tusker") {
    manaCost = "{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "182"
        artist = "Svetlin Velinov"
        flavorText = "\"And all this time I thought *we* were tracking *it*.\"\n—Juruk, Kalonian tracker"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/135946fc-fe67-401f-821d-d7145c63f030.jpg?1783939902"
    }
}
