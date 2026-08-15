package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Urborg Volcano
 * Land
 * This land enters tapped.
 * {T}: Add {B} or {R}.
 */
val UrborgVolcano = card("Urborg Volcano") {
    typeLine = "Land"
    colorIdentity = "BR"
    oracleText = "This land enters tapped.\n{T}: Add {B} or {R}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "330"
        artist = "Tony Szczudlo"
        flavorText = "Deep in the heart of Urborg lie massive volcanoes whose thick black smoke covers the land with perpetual darkness."
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c76f346c-ae34-4f5f-8e3b-6c77b0c4d530.jpg?1562935023"
    }
}
