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
 * The Fair Basilica
 * Land — Sphere
 *
 * This land enters tapped.
 * {T}: Add {W}.
 * {1}{W}, {T}, Sacrifice this land: Draw a card.
 */
val TheFairBasilica = card("The Fair Basilica") {
    colorIdentity = "W"
    typeLine = "Land — Sphere"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {W}.\n" +
        "{1}{W}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "252"
        artist = "Marc Simonetti"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/01d6ba55-7bc0-41c6-84be-8cd528e46a05.jpg?1783917982"
    }
}
