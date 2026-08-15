package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Prowling Caracal
 * {1}{W}
 * Creature — Cat
 * 3/1
 *
 * Vanilla — no rules text.
 */
val ProwlingCaracal = card("Prowling Caracal") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Jonathan Kuo"
        flavorText = "A hunter in the city requires the utmost cunning to survive. It must pounce only if the kill is certain, and leave the remains where no one will see."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5382fc4-3384-449e-a83f-43a59158d55b.jpg?1783933719"
    }
}
