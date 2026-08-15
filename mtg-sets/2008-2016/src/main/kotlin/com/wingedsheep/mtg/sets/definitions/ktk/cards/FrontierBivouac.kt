package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Frontier Bivouac
 * Land
 * This land enters tapped.
 * {T}: Add {G}, {U}, or {R}.
 */
val FrontierBivouac = card("Frontier Bivouac") {
    typeLine = "Land"
    colorIdentity = "URG"
    oracleText = "This land enters tapped.\n{T}: Add {G}, {U}, or {R}."

    replacementEffect(EntersTapped())

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

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Titus Lunter"
        flavorText = "\"The most powerful dreams visit those who shelter in a dragon's skull.\" —Chianul, Who Whispers Twice"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4335951-e73e-45cb-b2a5-6e9d14ba87ee.jpg?1562795027"
    }
}
