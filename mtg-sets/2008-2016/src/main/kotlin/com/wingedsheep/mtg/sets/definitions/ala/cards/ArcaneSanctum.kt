package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Arcane Sanctum
 * Land
 * This land enters tapped.
 * {T}: Add {W}, {U}, or {B}.
 *
 * One of the Alara "tri-lands": no basic land types, so the three mana abilities are authored
 * explicitly rather than derived from the type line.
 */
val ArcaneSanctum = card("Arcane Sanctum") {
    manaCost = ""
    colorIdentity = "WUB"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {W}, {U}, or {B}."

    replacementEffect(EntersTapped())

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
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "220"
        artist = "Anthony Francisco"
        flavorText = "\"We must rely on our own knowledge, not on the dogma of the seekers or the mutterings of the sphinxes.\"\n—Tullus of Palandius"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6edc0681-4252-4d3d-baf3-f03c22af1208.jpg"
    }
}
