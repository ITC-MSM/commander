package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Razortrap Gorge
 * Land
 *
 * This land enters tapped unless a player has 13 or less life.
 * {T}: Add {B} or {R}.
 */
val RazortrapGorge = card("Razortrap Gorge") {
    typeLine = "Land"
    colorIdentity = "BR"
    oracleText = "This land enters tapped unless a player has 13 or less life.\n{T}: Add {B} or {R}."

    replacementEffect(
        EntersTapped(
            unlessCondition = Conditions.APlayerLifeAtMost(13)
        )
    )

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "267"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98d0d067-b52d-47ec-ba7b-8cfcd716c0e5.jpg?1726286871"
    }
}
