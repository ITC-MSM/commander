package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Three Tree Rootweaver
 * {1}{G}
 * Creature — Mole Druid
 * 1/3
 *
 * {T}: Add one mana of any color.
 */
val ThreeTreeRootweaver = card("Three Tree Rootweaver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Mole Druid"
    power = 1
    toughness = 3
    oracleText = "{T}: Add one mana of any color."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Chris Seaman"
        flavorText = "\"It's easy to find your way around the Root Maze if you know what to look, listen, smell, feel, and taste for.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1ab6e14-26e0-4174-b5c6-bc0f5c26b177.jpg?1721426963"
    }
}
