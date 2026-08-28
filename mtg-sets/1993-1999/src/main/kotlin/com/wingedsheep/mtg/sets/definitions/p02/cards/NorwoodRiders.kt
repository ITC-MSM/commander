package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan

/**
 * Norwood Riders
 * {3}{G}
 * Creature — Elf
 * 3/3
 *
 * This creature can't be blocked by more than one creature.
 */
val NorwoodRiders = card("Norwood Riders") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf"
    oracleText = "This creature can't be blocked by more than one creature."
    power = 3
    toughness = 3

    staticAbility {
        ability = CantBeBlockedByMoreThan(maxBlockers = 1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Rebecca Guay"
        flavorText = "\"Trade my moose? Sure—when I find a *horse* that can spear ten goblins at a time!\""
        imageUri = "https://cards.scryfall.io/normal/front/9/0/904ba8db-853e-4f51-acfe-83e472524380.jpg"
    }
}
