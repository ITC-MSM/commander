package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Standing Stones
 * {3}
 * Artifact
 * {1}, {T}, Pay 1 life: Add one mana of any color.
 */
val StandingStones = card("Standing Stones") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, {T}, Pay 1 life: Add one mana of any color."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.PayLife(1))
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "110"
        artist = "Sandra Everingham"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d4c853e-2231-4af2-bcb0-1781c18ec3be.jpg?1783947924"
    }
}
