package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Blighted Steppe
 * Land
 * {T}: Add {C}.
 * {3}{W}, {T}, Sacrifice this land: You gain 2 life for each creature you control.
 */
val BlightedSteppe = card("Blighted Steppe") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n" +
        "{3}{W}, {T}, Sacrifice this land: You gain 2 life for each creature you control."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.GainLife(
            DynamicAmount.Multiply(DynamicAmounts.creaturesYouControl(), 2),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "232"
        artist = "Yeong-Hao Han"
        flavorText = "\"When a limb is gangrenous, you cut it off. This is no different.\"\n" +
            "—Greenweaver Mina"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f43df8ee-d3e4-419e-aed0-1059d95f9cab.jpg?1783938175"
    }
}
