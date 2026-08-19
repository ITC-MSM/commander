package com.wingedsheep.mtg.sets.definitions.dst.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mirrodin's Core
 * Land
 *
 * {T}: Add {C}.
 * {T}: Put a charge counter on this land.
 * {T}, Remove a charge counter from this land: Add one mana of any color.
 */
val MirrodinsCore = card("Mirrodin's Core") {
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n{T}: Put a charge counter on this land.\n{T}, Remove a charge counter from this land: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
        description = "{T}: Put a charge counter on this land."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.RemoveCounterFromSelf(Counters.CHARGE))
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf0e60c9-1548-4981-ada1-6656804a772e.jpg?1783944413"
    }
}
