package com.wingedsheep.mtg.sets.definitions.jou.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Triton Shorestalker
 * {U}
 * Creature — Merfolk Rogue
 * 1/1
 * This creature can't be blocked.
 */
val TritonShorestalker = card("Triton Shorestalker") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Rogue"
    power = 1
    toughness = 1
    oracleText = "This creature can't be blocked."

    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Svetlin Velinov"
        flavorText = "\"Who will miss you, drywalker? A wife? A child? Perhaps they will blame the sea for your fate, and teach future generations to stay far away from it.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/8/881c554e-2324-41e2-89f1-db9f4017bc31.jpg?1783939440"
    }
}
