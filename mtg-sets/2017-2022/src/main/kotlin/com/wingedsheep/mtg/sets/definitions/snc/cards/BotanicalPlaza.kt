package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Botanical Plaza
 * Land
 * This land enters tapped.
 * {T}: Add {G} or {W}.
 * {2}{G}{W}, {T}, Sacrifice this land: Draw a card.
 */
val BotanicalPlaza = card("Botanical Plaza") {
    colorIdentity = "GW"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {G} or {W}.\n{2}{G}{W}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{G}{W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "247"
        artist = "Grady Frederick"
        flavorText = "Public parks are a favored meeting spot for those considering a shift in allegiance."
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f8a6f93-1fc8-4690-add4-538763277f8e.jpg?1783923058"
    }
}
