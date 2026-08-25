package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Azure Drake
 * {3}{U}
 * Creature — Drake
 * 2/4
 *
 * Flying
 */
val AzureDrake = card("Azure Drake") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 2
    toughness = 4
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Dan Frazier"
        flavorText = "The Azure Drake would be more powerful were it not so easily distracted."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb5f13a2-0896-4230-8957-6ad1cb2b895b.jpg?1783948078"
    }
}
