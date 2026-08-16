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
 * The Dross Pits
 * Land — Sphere
 *
 * This land enters tapped.
 * {T}: Add {B}.
 * {1}{B}, {T}, Sacrifice this land: Draw a card.
 */
val TheDrossPits = card("The Dross Pits") {
    colorIdentity = "B"
    typeLine = "Land — Sphere"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {B}.\n" +
        "{1}{B}, {T}, Sacrifice this land: Draw a card."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "251"
        artist = "Martin de Diego Sádaba"
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19d469f1-2219-4466-9f8a-769ee43e28db.jpg?1783917981"
    }
}
