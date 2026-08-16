package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Crumbling Necropolis
 * Land
 * This land enters tapped.
 * {T}: Add {U}, {B}, or {R}.
 *
 * One of the Alara "tri-lands": no basic land types, so the three mana abilities are authored
 * explicitly rather than derived from the type line.
 */
val CrumblingNecropolis = card("Crumbling Necropolis") {
    manaCost = ""
    colorIdentity = "UBR"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {U}, {B}, or {R}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Dave Kendall"
        flavorText = "\"They say the ruins of Sedraxis were once a shining capital in Vithia. Now it is a blight, a place to be avoided by the living.\"\n—Olcot, Rider of Joffik"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94da8e38-77a4-4d25-9e1f-f33c246f60c8.jpg"
    }
}
