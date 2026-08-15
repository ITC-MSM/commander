package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Leatherback Baloth
 * {G}{G}{G}
 * Creature — Beast
 * 4/5
 *
 * Vanilla — no rules text.
 */
val LeatherbackBaloth = card("Leatherback Baloth") {
    manaCost = "{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 5

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "107"
        artist = "Dave Kendall"
        flavorText = "Heavy enough to withstand the Roil, leatherback skeletons are havens for travelers in storms and landshifts."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55f97b4c-42c7-4986-a150-0b8de11f0537.jpg?1783942043"
    }
}
