package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Barbary Apes
 * {1}{G}
 * Creature — Ape
 * 2/2
 *
 * Vanilla — no rules text.
 */
val BarbaryApes = card("Barbary Apes") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ape"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Bryon Wackwitz"
        flavorText = "Unpredictable in the extreme, these carnivorous apes will prey even upon their own kind."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df25ffdd-995d-46ae-856b-f6368f9438ed.jpg?1783948049"
    }
}
