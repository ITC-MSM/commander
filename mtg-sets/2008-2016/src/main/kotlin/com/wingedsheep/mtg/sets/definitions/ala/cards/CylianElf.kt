package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cylian Elf
 * {1}{G}
 * Creature — Elf Scout
 * 2/2
 *
 * Vanilla — no rules text.
 */
val CylianElf = card("Cylian Elf") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Scout"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Steve Prescott"
        flavorText = "From her sunsail tent high above the forest floor, an elf harkener can hear the footfalls of a single creature through the cacophony of Naya's jungle sounds."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3afaab6-4768-4852-a0b6-4e6a0295bde7.jpg?1783942555"
    }
}
