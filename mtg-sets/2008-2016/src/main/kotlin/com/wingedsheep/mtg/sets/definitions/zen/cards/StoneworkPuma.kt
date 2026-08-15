package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stonework Puma
 * {3}
 * Artifact Creature — Cat Ally
 * 2/2
 *
 * Vanilla — no rules text.
 */
val StoneworkPuma = card("Stonework Puma") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Cat Ally"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "207"
        artist = "Christopher Moeller"
        flavorText = "\"We suffer uneasy ground, unstable alliances, and unpredictable magic. Something you can truly trust is worth more than a chest of gold.\"\n—Nikou, Joraga bard"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05460615-4c24-487f-841a-ca14106e5688.jpg?1783942125"
    }
}
