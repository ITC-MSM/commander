package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Salvaged Manaworker
 * {2}
 * Artifact Creature — Construct
 * 1/3
 * {1}: Add one mana of any color. Activate only once each turn.
 */
val SalvagedManaworker = card("Salvaged Manaworker") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    oracleText = "{1}: Add one mana of any color. Activate only once each turn."
    power = 1
    toughness = 3

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "237"
        artist = "Julian Kok Joon Wen"
        flavorText = "\"When weapons of war are repurposed for peaceful ends, you know society has advanced.\"\n—Jodah"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5fc22ecb-5d87-464e-8c7d-5d40a52e1e4f.jpg?1783921267"
    }
}
