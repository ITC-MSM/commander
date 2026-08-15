package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ironclad Krovod
 * {3}{W}
 * Creature — Beast
 * 2/5
 *
 * Vanilla — no rules text.
 */
val IroncladKrovod = card("Ironclad Krovod") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Sam Rowan"
        flavorText = "\"We need to block the exits from the plaza! What's big, heavy, and available?\"\n—Gideon Jura"
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afb16895-6542-405e-9793-154ffc439f23.jpg?1783933481"
    }
}
