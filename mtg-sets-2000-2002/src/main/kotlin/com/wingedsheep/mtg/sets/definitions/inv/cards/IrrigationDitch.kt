package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Irrigation Ditch
 * Land
 * This land enters tapped.
 * {T}: Add {W}.
 * {T}, Sacrifice this land: Add {G}{U}.
 */
val IrrigationDitch = card("Irrigation Ditch") {
    typeLine = "Land"
    colorIdentity = "GWU"
    oracleText = "This land enters tapped.\n{T}: Add {W}.\n{T}, Sacrifice this land: Add {G}{U}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.GREEN),
            Effects.AddMana(Color.BLUE),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "324"
        artist = "Rob Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/9/7/977f1b44-166c-4faf-8a7b-d431707e90ce.jpg?1562925548"
    }
}
