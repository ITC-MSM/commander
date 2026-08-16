package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan

/**
 * Bristling Boar
 * {3}{G}
 * Creature — Boar
 * 4/3
 * This creature can't be blocked by more than one creature.
 */
val BristlingBoar = card("Bristling Boar") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    power = 4
    toughness = 3
    oracleText = "This creature can't be blocked by more than one creature."

    staticAbility {
        ability = CantBeBlockedByMoreThan(maxBlockers = 1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Svetlin Velinov"
        flavorText = "\"Nicol Bolas destroyed my world. I owe it to Skalla to celebrate all life, no matter how dangerous.\" —Vivien Reid"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/999030b2-2f91-4c45-981f-acdbbf9034af.jpg?1783934541"
        ruling("2020-04-17", "If Bristling Boar gains menace, it can't be blocked at all.")
    }
}
