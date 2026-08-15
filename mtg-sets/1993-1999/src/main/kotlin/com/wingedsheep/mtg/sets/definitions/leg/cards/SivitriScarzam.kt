package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sivitri Scarzam
 * {5}{U}{B}
 * Legendary Creature — Human
 * 6/4
 *
 * Vanilla — no rules text.
 */
val SivitriScarzam = card("Sivitri Scarzam") {
    manaCost = "{5}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Human"
    power = 6
    toughness = 4

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "258"
        artist = "NéNé Thomas"
        flavorText = "Even the brave have cause to tremble at the sight of Sivitri Scarzam. Who else has tamed Scarzam's Dragon?"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c12ee9e-db13-4b4d-a061-b6566f538f09.jpg?1783948032"
    }
}
