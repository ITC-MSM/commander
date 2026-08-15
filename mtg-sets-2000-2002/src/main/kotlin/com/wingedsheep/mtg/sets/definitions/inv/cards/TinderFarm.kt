package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Tinder Farm
 * Land
 * This land enters tapped.
 * {T}: Add {G}.
 * {T}, Sacrifice this land: Add {R}{W}.
 */
val TinderFarm = card("Tinder Farm") {
    typeLine = "Land"
    colorIdentity = "GRW"
    oracleText = "This land enters tapped.\n{T}: Add {G}.\n{T}, Sacrifice this land: Add {R}{W}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.RED),
            Effects.AddMana(Color.WHITE),
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "329"
        artist = "Rob Alexander"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/989b5901-aeb0-4a48-8c53-3b0ec0e0deba.jpg?1562925784"
    }
}
