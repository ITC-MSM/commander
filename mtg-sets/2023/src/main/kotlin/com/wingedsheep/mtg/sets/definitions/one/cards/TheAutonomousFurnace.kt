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
 * The Autonomous Furnace
 * Land — Sphere
 *
 * This land enters tapped.
 * {T}: Add {R}.
 * {1}{R}, {T}, Sacrifice this land: Draw a card.
 */
val TheAutonomousFurnace = card("The Autonomous Furnace") {
    colorIdentity = "R"
    typeLine = "Land — Sphere"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {R}.\n" +
        "{1}{R}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "247"
        artist = "Muhammad Firdaus"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c16f96f5-a2a6-4ac4-bdae-326cee92bf2e.jpg?1783917984"
    }
}
