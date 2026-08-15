package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Hidden Courtyard
 * Land — Cave
 * This land enters tapped.
 * {T}: Add {W}.
 * {4}{W}, {T}, Sacrifice this land: Discover 4. Activate only as a sorcery.
 */
val HiddenCourtyard = card("Hidden Courtyard") {
    typeLine = "Land — Cave"
    colorIdentity = "W"
    oracleText = "This land enters tapped.\n{T}: Add {W}.\n{4}{W}, {T}, Sacrifice this land: Discover 4. Activate only as a sorcery."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Discover(4)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "274"
        artist = "Josu Solano"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8685d46-99fc-44b3-be95-707a4b7b8327.jpg?1782694392"
    }
}
