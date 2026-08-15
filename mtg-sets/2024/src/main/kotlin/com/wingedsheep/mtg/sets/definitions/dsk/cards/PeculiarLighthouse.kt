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
 * Peculiar Lighthouse
 * Land
 *
 * This land enters tapped unless a player has 13 or less life.
 * {T}: Add {U} or {R}.
 */
val PeculiarLighthouse = card("Peculiar Lighthouse") {
    typeLine = "Land"
    colorIdentity = "UR"
    oracleText = "This land enters tapped unless a player has 13 or less life.\n{T}: Add {U} or {R}."

    replacementEffect(
        EntersTapped(
            unlessCondition = Conditions.APlayerLifeAtMost(13)
        )
    )

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
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
        collectorNumber = "265"
        artist = "Raymond Bonilla"
        flavorText = "They say its beckoning beacon leads not to safe harbor but into the arms of the drowned."
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a6e40c0-e70e-4353-a920-9851cfac71dd.jpg?1726286864"
    }
}
