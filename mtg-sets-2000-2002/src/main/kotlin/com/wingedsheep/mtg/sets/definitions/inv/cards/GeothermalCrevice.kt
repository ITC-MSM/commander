package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Geothermal Crevice
 * Land
 * This land enters tapped.
 * {T}: Add {R}.
 * {T}, Sacrifice this land: Add {B}{G}.
 */
val GeothermalCrevice = card("Geothermal Crevice") {
    typeLine = "Land"
    colorIdentity = "BGR"
    oracleText = "This land enters tapped.\n{T}: Add {R}.\n{T}, Sacrifice this land: Add {B}{G}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.BLACK),
            Effects.AddMana(Color.GREEN),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "323"
        artist = "John Avon"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e744b593-13fe-4967-b492-ac02f5815e57.jpg?1562941417"
    }
}
