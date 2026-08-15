package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Sulfur Vent
 * Land
 * This land enters tapped.
 * {T}: Add {B}.
 * {T}, Sacrifice this land: Add {U}{R}.
 */
val SulfurVent = card("Sulfur Vent") {
    typeLine = "Land"
    colorIdentity = "UBR"
    oracleText = "This land enters tapped.\n{T}: Add {B}.\n{T}, Sacrifice this land: Add {U}{R}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.BLUE),
            Effects.AddMana(Color.RED),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "328"
        artist = "Edward P. Beard, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22c66ed6-55fb-4c65-aac4-26d9cc3053b8.jpg?1562901960"
    }
}
