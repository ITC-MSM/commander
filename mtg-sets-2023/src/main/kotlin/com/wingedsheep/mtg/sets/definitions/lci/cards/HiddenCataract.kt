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
 * Hidden Cataract
 * Land — Cave
 * This land enters tapped.
 * {T}: Add {U}.
 * {4}{U}, {T}, Sacrifice this land: Discover 4. Activate only as a sorcery.
 */
val HiddenCataract = card("Hidden Cataract") {
    typeLine = "Land — Cave"
    colorIdentity = "U"
    oracleText = "This land enters tapped.\n{T}: Add {U}.\n{4}{U}, {T}, Sacrifice this land: Discover 4. Activate only as a sorcery."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Discover(4)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "273"
        artist = "Josu Solano"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69f317fc-f603-45b5-9208-545be4dcbf36.jpg?1782694393"
    }
}
