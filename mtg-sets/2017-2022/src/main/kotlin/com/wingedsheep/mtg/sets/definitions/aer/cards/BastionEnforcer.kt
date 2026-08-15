package com.wingedsheep.mtg.sets.definitions.aer.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bastion Enforcer
 * {2}{W}
 * Creature — Dwarf Soldier
 * 3/2
 *
 * Vanilla — no rules text.
 */
val BastionEnforcer = card("Bastion Enforcer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Soldier"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Matt Stewart"
        flavorText = "Headquartered at the Bastion of the Honorable, the Consulate's enforcers are charged with the impossible task of keeping the peace."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88b9c0f7-d49b-4d74-9038-44954054ce21.jpg?1783936782"
    }
}
