package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction

/**
 * Humble Naturalist
 * {1}{G}
 * Creature — Human Druid
 * 1/3
 * {T}: Add one mana of any color. Spend this mana only to cast a creature spell.
 */
val HumbleNaturalist = card("Humble Naturalist") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    power = 1
    toughness = 3
    oracleText = "{T}: Add one mana of any color. Spend this mana only to cast a creature spell."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddManaOfChoice(restriction = ManaRestriction.CreatureSpellsOnly)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Matt Stewart"
        flavorText = "\"The key to bonding with monsters, no matter their size, is knowing where they like to be scratched.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f56705d-eb64-4cef-b716-edbeac60bf79.jpg"
    }
}
