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

val VigorbloomAnnex = card("Vigorbloom Annex") {
    manaCost = ""
    colorIdentity = "GW"
    typeLine = "Land"
    oracleText = "This land enters tapped unless you control a planeswalker.\n{T}: Add {G} or {W}."

    replacementEffect(EntersTapped(unlessCondition = Conditions.YouControl(GameObjectFilter.Planeswalker)))

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

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "194"
        artist = "Sergey Glushakov"
        flavorText = "Battlemages of Vigorbloom, the school of invasive healing, tend to the health of Hexhaven, no matter the cost."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db8c7bdd-76cd-4be0-ae0d-d430e9a5fe7a.jpg?1789557019"
        inBooster = false
    }
}
