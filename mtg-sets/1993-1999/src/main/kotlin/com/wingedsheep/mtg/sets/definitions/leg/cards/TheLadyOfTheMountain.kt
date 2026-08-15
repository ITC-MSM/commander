package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * The Lady of the Mountain
 * {4}{R}{G}
 * Legendary Creature — Giant
 * 5/5
 *
 * Vanilla — no rules text.
 */
val TheLadyOfTheMountain = card("The Lady of the Mountain") {
    manaCost = "{4}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Giant"
    power = 5
    toughness = 5

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "263"
        artist = "Richard Kane Ferguson"
        flavorText = "Her given name has been lost in the mists of time. Legend says that her silent vigil will one day be ended by the one who, pure of heart and spirit, calls out that name again."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83717eb2-220e-4086-be09-dee9174798b8.jpg?1783948032"
    }
}
