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

val StingerquillAnnex = card("Stingerquill Annex") {
    manaCost = ""
    colorIdentity = "BR"
    typeLine = "Land"
    oracleText = "This land enters tapped unless you control a planeswalker.\n{T}: Add {B} or {R}."

    replacementEffect(EntersTapped(unlessCondition = Conditions.YouControl(GameObjectFilter.Planeswalker)))

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
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Sergey Glushakov"
        flavorText = "Battlemages of Stingerquill, the school of painful words, sharpen their tongues to cut down the unworthy."
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6ede3143-69ac-4cbe-922a-d25b07c26da7.jpg?1789557046"
        inBooster = false
    }
}
