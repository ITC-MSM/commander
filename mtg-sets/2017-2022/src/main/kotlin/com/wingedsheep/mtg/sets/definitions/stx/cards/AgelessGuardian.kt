package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ageless Guardian
 * {1}{W}
 * Creature — Spirit Soldier
 * 1/4
 *
 * Vanilla — no rules text.
 */
val AgelessGuardian = card("Ageless Guardian") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Soldier"
    power = 1
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Nicholas Gregory"
        flavorText = "Ancient ruins dot the world of Arcavios, most older than Strixhaven itself and many still guarded by soldiers from the Blood Age, centuries after their deaths."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a5ff6af-402f-4bf6-a75b-dc1e0e40aff6.jpg?1783927397"
    }
}
