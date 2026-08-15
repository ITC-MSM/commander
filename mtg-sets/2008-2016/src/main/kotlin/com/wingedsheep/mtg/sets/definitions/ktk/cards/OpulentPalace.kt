package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Opulent Palace
 * Land
 * This land enters tapped.
 * {T}: Add {B}, {G}, or {U}.
 */
val OpulentPalace = card("Opulent Palace") {
    typeLine = "Land"
    colorIdentity = "UBG"
    oracleText = "This land enters tapped.\n{T}: Add {B}, {G}, or {U}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

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
        rarity = Rarity.UNCOMMON
        collectorNumber = "238"
        artist = "Adam Paquette"
        flavorText = "The dense jungle surrenders to a lush and lavish expanse. At its center uncoil the spires of Qarsi Palace."
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21326575-80b9-4a4e-a93c-6880ec6575d5.jpg?1562783524"
    }
}
