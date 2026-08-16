package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Charismatic Vanguard
 * {2}{W}
 * Creature — Dwarf Soldier
 * 3/2
 * {4}{W}: Creatures you control get +1/+1 until end of turn.
 */
val CharismaticVanguard = card("Charismatic Vanguard") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Soldier"
    oracleText = "{4}{W}: Creatures you control get +1/+1 until end of turn."
    power = 3
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{4}{W}")
        effect = Patterns.Group.modifyStatsForAll(1, 1, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "David Palumbo"
        flavorText = "\"This battlefield is an anvil, my mace a forging hammer . . . and you, Phyrexian scum, are the scrap metal that's about to be reshaped as I see fit.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a51764fe-0d75-4cfa-a699-0d9e7ffb7843.jpg?1783921370"
    }
}
