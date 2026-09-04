package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Vessel of Volatility (Shadows over Innistrad #189)
 * {1}{R}
 * Enchantment
 *
 * {1}{R}, Sacrifice this enchantment: Add {R}{R}{R}{R}.
 */
val VesselOfVolatility = card("Vessel of Volatility") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "{1}{R}, Sacrifice this enchantment: Add {R}{R}{R}{R}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.SacrificeSelf)
        effect = Effects.AddMana(Color.RED, 4)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Kieran Yanner"
        flavorText = "\"To be honest, I'm not quite sure what's going to happen.\"\n—Renna, Selhoff alchemist"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81647b86-2c84-4a14-8d5a-919f7a5b8bc7.jpg?1783937739"
    }
}
