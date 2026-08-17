package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Tramway Station
 * Land
 * This land enters tapped.
 * {T}: Add {B} or {R}.
 * {2}{B}{R}, {T}, Sacrifice this land: Draw a card.
 */
val TramwayStation = card("Tramway Station") {
    colorIdentity = "BR"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {B} or {R}.\n{2}{B}{R}, {T}, Sacrifice this land: Draw a card."

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

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}{R}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "258"
        artist = "Alexander Forssberg"
        flavorText = "A ten-minute ride can take you to the luxury of Park Heights, or the grit of the Caldaia."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0532304-da6d-45cd-b1e6-4779d3f3b2bc.jpg?1783923053"
    }
}
