package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Catalyst Elemental
 * {2}{R}
 * Creature — Elemental
 * 2/2
 * Sacrifice this creature: Add {R}{R}.
 *
 * A mana ability (no target, no stack) — cf. Blood Vassal, the same sacrifice-for-two-mana shape.
 */
val CatalystElemental = card("Catalyst Elemental") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
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
        collectorNumber = "132"
        artist = "Deruchenko Alexander"
        flavorText = "As the hyperstormic generator crept past redline, a being emerged from the arc."
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e83a678b-19d4-47a5-aa1c-c2437e5009c0.jpg?1783934556"
    }
}
