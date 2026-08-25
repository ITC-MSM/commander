package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Amrou Kithkin
 * {W}{W}
 * Creature — Kithkin
 * 1/1
 *
 * This creature can't be blocked by creatures with power 3 or greater.
 */
val AmrouKithkin = card("Amrou Kithkin") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin"
    power = 1
    toughness = 1
    oracleText = "This creature can't be blocked by creatures with power 3 or greater."

    staticAbility {
        ability = CantBeBlockedBy(blockerFilter = GameObjectFilter.Creature.powerAtLeast(3))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Quinton Hoover"
        flavorText = "Quick and agile, Amrou Kithkin can usually escape from even the most fearsome opponents."
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbce1c55-123c-4a05-bde4-18a1601fcc5a.jpg?1783948088"
    }
}
