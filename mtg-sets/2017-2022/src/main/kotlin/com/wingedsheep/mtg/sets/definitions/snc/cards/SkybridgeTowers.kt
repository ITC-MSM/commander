package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Skybridge Towers
 * Land
 * This land enters tapped.
 * {T}: Add {W} or {U}.
 * {2}{W}{U}, {T}, Sacrifice this land: Draw a card.
 */
val SkybridgeTowers = card("Skybridge Towers") {
    colorIdentity = "UW"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {W} or {U}.\n{2}{W}{U}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}{U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "256"
        artist = "Muhammad Firdaus"
        flavorText = "New Capenna's districts are a series of tiers; wealth and power always rise to the top."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e28c871f-a96a-4e7d-a159-2e93aeb276d4.jpg?1783923054"
    }
}
