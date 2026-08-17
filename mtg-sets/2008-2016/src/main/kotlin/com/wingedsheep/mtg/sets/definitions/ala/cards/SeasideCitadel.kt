package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Seaside Citadel
 * Land
 * This land enters tapped.
 * {T}: Add {G}, {W}, or {U}.
 */
val SeasideCitadel = card("Seaside Citadel") {
    typeLine = "Land"
    colorIdentity = "GWU"
    oracleText = "This land enters tapped.\n{T}: Add {G}, {W}, or {U}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
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
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Volkan Baǵa"
        flavorText = "For wisdom's sake, it was built high to gaze on all things. For glory's sake, it was built high as a testament of power. For strength's sake, it was built high to repel all attacks."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1995d2a-4550-4c84-ad44-183b06579e98.jpg?1783942531"
    }
}
