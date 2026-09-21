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

val FateholdAnnex = card("Fatehold Annex") {
    manaCost = ""
    colorIdentity = "UW"
    typeLine = "Land"
    oracleText = "This land enters tapped unless you control a planeswalker.\n{T}: Add {W} or {U}."

    replacementEffect(EntersTapped(unlessCondition = Conditions.YouControl(GameObjectFilter.Planeswalker)))

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
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Sergey Glushakov"
        flavorText = "Battlemages of Fatehold, the school of future history, sift through infinite potentials to write the future they desire."
        imageUri = "https://cards.scryfall.io/normal/front/5/1/5140f962-62f3-40fd-a322-44896c7e2613.jpg?1789556955"
        inBooster = false
    }
}
