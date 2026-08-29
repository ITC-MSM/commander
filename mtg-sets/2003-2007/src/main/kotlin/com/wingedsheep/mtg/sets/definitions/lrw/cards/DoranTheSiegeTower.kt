package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AssignDamageEqualToToughness
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Doran, the Siege Tower
 * {W}{B}{G}
 * Legendary Creature — Treefolk Shaman
 * 0/5
 * Each creature assigns combat damage equal to its toughness rather than its power.
 *
 * The static is unconditional and unrestricted — every creature on the battlefield, whoever
 * controls it, Doran included. That is [GroupFilter.AllCreatures] with
 * `onlyWhenToughnessGreaterThanPower = false`; the conditional form is
 * [com.wingedsheep.mtg.sets.definitions.ecl.cards.BarkOfDoran]'s, not Doran's own.
 */
val DoranTheSiegeTower = card("Doran, the Siege Tower") {
    manaCost = "{W}{B}{G}"
    colorIdentity = "WBG"
    typeLine = "Legendary Creature — Treefolk Shaman"
    power = 0
    toughness = 5
    oracleText = "Each creature assigns combat damage equal to its toughness rather than its power."

    staticAbility {
        ability = AssignDamageEqualToToughness(
            filter = GroupFilter.AllCreatures,
            onlyWhenToughnessGreaterThanPower = false,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "247"
        artist = "Mark Zug"
        flavorText = "\"Each year that passes rings you inwardly with memory and might. " +
            "Wield your heart, and the world will tremble.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b006b169-295d-4ead-8e8e-29a9c3246025.jpg?1783942855"
    }
}
