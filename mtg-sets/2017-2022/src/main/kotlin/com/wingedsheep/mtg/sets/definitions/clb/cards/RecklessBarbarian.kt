package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Reckless Barbarian
 * {1}{R}
 * Creature — Dragon Barbarian
 * 2/2
 * Sacrifice this creature: Add {R}{R}.
 *
 * Catalyst Elemental's shape exactly: a mana ability, so [Costs.SacrificeSelf] pays for
 * [Effects.AddMana] with no target and no stack — the `manaAbility` flag is what makes the engine
 * treat it as one, and it carries the [TimingRule.ManaAbility] timing with it.
 */
val RecklessBarbarian = card("Reckless Barbarian") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon Barbarian"
    power = 2
    toughness = 2
    oracleText = "Sacrifice this creature: Add {R}{R}."

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.AddMana(Color.RED, 2)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Oleksandr Kozachenko"
        flavorText = "The best defense is not even knowing what the word \"defense\" means."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c912e984-1d27-4da2-9733-d56e437bcf58.jpg?1783922731"
    }
}
