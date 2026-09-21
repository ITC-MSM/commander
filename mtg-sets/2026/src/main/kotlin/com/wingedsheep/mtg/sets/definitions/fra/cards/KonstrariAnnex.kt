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

val KonstrariAnnex = card("Konstrari Annex") {
    manaCost = ""
    colorIdentity = "GR"
    typeLine = "Land"
    oracleText = "This land enters tapped unless you control a planeswalker.\n{T}: Add {R} or {G}."

    replacementEffect(EntersTapped(unlessCondition = Conditions.YouControl(GameObjectFilter.Planeswalker)))

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Sergey Glushakov"
        flavorText = "Battlemages of Konstrari, the school of constructive arts, pour their hearts into their craft in defense of Hexhaven."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39c805e3-82cd-42a9-80fe-8d81712a94ea.jpg?1789556993"
        inBooster = false
    }
}
