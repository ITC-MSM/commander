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
 * Lakeside Shack
 * Land
 *
 * This land enters tapped unless a player has 13 or less life.
 * {T}: Add {G} or {U}.
 */
val LakesideShack = card("Lakeside Shack") {
    typeLine = "Land"
    colorIdentity = "GU"
    oracleText = "This land enters tapped unless a player has 13 or less life.\n{T}: Add {G} or {U}."

    replacementEffect(
        EntersTapped(
            unlessCondition = Conditions.APlayerLifeAtMost(13)
        )
    )

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "262"
        artist = "Bartek Fedyczak"
        flavorText = "They say that if you stay in the cabin until the mists reach it, something will slither from the water and knock at the door."
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9367acd-393a-4966-ba60-af2ecd4e7596.jpg?1726286852"
    }
}
