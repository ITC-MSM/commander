package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Shivan Oasis
 * Land
 * This land enters tapped.
 * {T}: Add {R} or {G}.
 */
val ShivanOasis = card("Shivan Oasis") {
    typeLine = "Land"
    colorIdentity = "RG"
    oracleText = "This land enters tapped.\n{T}: Add {R} or {G}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "327"
        artist = "Rob Alexander"
        flavorText = "Only the hardiest explorers survive to eat the fruit."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/9841f7e8-162c-44a3-96f3-af944fce15d1.jpg?1562925740"
    }
}
