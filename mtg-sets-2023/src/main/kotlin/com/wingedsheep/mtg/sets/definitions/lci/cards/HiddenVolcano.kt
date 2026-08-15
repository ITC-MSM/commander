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
 * Hidden Volcano
 * Land — Cave
 * This land enters tapped.
 * {T}: Add {R}.
 * {4}{R}, {T}, Sacrifice this land: Discover 4. Activate only as a sorcery.
 */
val HiddenVolcano = card("Hidden Volcano") {
    typeLine = "Land — Cave"
    colorIdentity = "R"
    oracleText = "This land enters tapped.\n{T}: Add {R}.\n{4}{R}, {T}, Sacrifice this land: Discover 4. Activate only as a sorcery."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{R}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Discover(4)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "277"
        artist = "Logan Feliciano"
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9fa06aed-52c1-48f1-9906-362db12a3cf7.jpg?1782694391"
    }
}
