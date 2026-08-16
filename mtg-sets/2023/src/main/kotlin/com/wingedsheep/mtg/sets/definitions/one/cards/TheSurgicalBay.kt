package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * The Surgical Bay
 * Land — Sphere
 *
 * This land enters tapped.
 * {T}: Add {U}.
 * {1}{U}, {T}, Sacrifice this land: Draw a card.
 */
val TheSurgicalBay = card("The Surgical Bay") {
    colorIdentity = "U"
    typeLine = "Land — Sphere"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {U}.\n" +
        "{1}{U}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "260"
        artist = "Sarah Finnigan"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b49ec35-2c4f-4144-85fe-226f7cb67266.jpg?1783917979"
    }
}
