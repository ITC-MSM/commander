package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Woolly Thoctar
 * {R}{G}{W}
 * Creature — Beast
 * 5/4
 *
 * Vanilla — no rules text.
 */
val WoollyThoctar = card("Woolly Thoctar") {
    manaCost = "{R}{G}{W}"
    colorIdentity = "WRG"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 4

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Wayne Reynolds"
        flavorText = "One of the most ferocious and deadly gargantuans, the thoctar never sees its worshippers, but it often awakens surrounded by gifts and sacrifices."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d5907d5-ae5c-4c9d-a5df-61f1c94f979d.jpg?1783942536"
    }
}
