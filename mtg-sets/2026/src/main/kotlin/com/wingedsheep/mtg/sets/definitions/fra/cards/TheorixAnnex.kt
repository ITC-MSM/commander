package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

val TheorixAnnex = card("Theorix Annex") {
    manaCost = ""
    colorIdentity = "BU"
    typeLine = "Land"
    oracleText = "This land enters tapped unless you control a planeswalker.\n{T}: Add {U} or {B}."

    replacementEffect(EntersTapped(unlessCondition = Conditions.YouControl(GameObjectFilter.Planeswalker)))

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
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Sergey Glushakov"
        flavorText = "Battlemages of Theorix, the school of esoteric mathematics, reshape reality itself to their whim."
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97bbbd23-ecb1-4407-ac14-dede08532a1e.jpg?1789557008"
        inBooster = false
    }
}
