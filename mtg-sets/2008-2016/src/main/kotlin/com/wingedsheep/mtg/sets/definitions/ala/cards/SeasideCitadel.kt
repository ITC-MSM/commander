package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Seaside Citadel
 * Land
 * This land enters tapped.
 * {T}: Add {G}, {W}, or {U}.
 *
 * One of the Alara "tri-lands": no basic land types, so the three mana abilities are authored
 * explicitly rather than derived from the type line.
 */
val SeasideCitadel = card("Seaside Citadel") {
    manaCost = ""
    colorIdentity = "WUG"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {G}, {W}, or {U}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Volkan Baǵa"
        flavorText = "For wisdom's sake, it was built high to gaze on all things. For glory's sake, it was built high as a testament of power. For strength's sake, it was built high to repel all attacks."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1995d2a-4550-4c84-ad44-183b06579e98.jpg"
    }
}
