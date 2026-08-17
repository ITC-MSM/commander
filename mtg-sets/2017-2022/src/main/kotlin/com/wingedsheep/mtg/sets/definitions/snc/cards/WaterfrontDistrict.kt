package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Waterfront District
 * Land
 * This land enters tapped.
 * {T}: Add {U} or {B}.
 * {2}{U}{B}, {T}, Sacrifice this land: Draw a card.
 */
val WaterfrontDistrict = card("Waterfront District") {
    colorIdentity = "BU"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {U} or {B}.\n{2}{U}{B}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{U}{B}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "259"
        artist = "Alexander Forssberg"
        flavorText = "The bottom of the dark canals is littered with countless corpses and treasures."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/debf7aac-4d31-49b4-955b-79036258df69.jpg?1783923052"
    }
}
