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
 * Hidden Necropolis
 * Land — Cave
 * This land enters tapped.
 * {T}: Add {B}.
 * {4}{B}, {T}, Sacrifice this land: Discover 4. Activate only as a sorcery.
 */
val HiddenNecropolis = card("Hidden Necropolis") {
    typeLine = "Land — Cave"
    colorIdentity = "B"
    oracleText = "This land enters tapped.\n{T}: Add {B}.\n{4}{B}, {T}, Sacrifice this land: Discover 4. Activate only as a sorcery."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{B}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Discover(4)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "275"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f67fd04f-05da-4418-97de-abeb7346cc69.jpg?1782694392"
    }
}
