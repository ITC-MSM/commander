package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rumbling Baloth
 * {2}{G}{G}
 * Creature — Beast
 * 4/4
 *
 * Vanilla — no rules text.
 */
val RumblingBaloth = card("Rumbling Baloth") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Jesper Ejsing"
        flavorText = "In the dim light beneath the vast trees of Deepglade, baloths prowl in search of prey. Their guttural calls are more felt than heard, but their attack scream carries for miles."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d8610ff1-064b-4c75-a8df-d3b076370d1e.jpg?1783939900"
    }
}
