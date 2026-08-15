package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Memorial to Genius
 * Land
 * Memorial to Genius enters the battlefield tapped.
 * {T}: Add {U}.
 * {4}{U}, {T}, Sacrifice Memorial to Genius: Draw two cards.
 */
val MemorialToGenius = card("Memorial to Genius") {
    typeLine = "Land"
    colorIdentity = "U"
    oracleText = "Memorial to Genius enters the battlefield tapped.\n{T}: Add {U}.\n{4}{U}, {T}, Sacrifice Memorial to Genius: Draw two cards."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{U}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "243"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b97bb62e-45cf-4862-b181-5af463a442b5.jpg?1562741840"
    }
}
