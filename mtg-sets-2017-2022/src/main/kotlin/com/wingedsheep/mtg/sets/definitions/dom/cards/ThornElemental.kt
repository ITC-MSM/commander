package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AssignCombatDamageAsUnblocked

/**
 * Thorn Elemental
 * {5}{G}{G}
 * Creature — Elemental
 * 7/7
 * You may have Thorn Elemental assign its combat damage as though it weren't blocked.
 */
val ThornElemental = card("Thorn Elemental") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 7
    toughness = 7
    oracleText = "You may have Thorn Elemental assign its combat damage as though it weren't blocked."

    staticAbility {
        ability = AssignCombatDamageAsUnblocked()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "185"
        artist = "Daarken"
        flavorText = "The first law of Yavimaya is that guests may kill only to survive. As fires aren't needed in the warm weather, cutting trees for wood means death."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da901037-20e6-4445-8e7e-1ccd2e8b13ae.jpg?1562743950"
    }
}
