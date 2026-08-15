package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule


/**
 * Goobbue Gardener
 * {1}{G}
 * Creature — Plant Beast
 * 1/3
 * {T}: Add {G}.
 */
val GoobbueGardener = card("Goobbue Gardener") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Beast"
    oracleText = "{T}: Add {G}."
    power = 1
    toughness = 3
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "188"
        artist = "Janna Sophia"
        flavorText = "The parasitic moss that grows in the folds of the goobbue's back is highly valued as an ingredient in various potions and medicines."
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b7c3544a-5dd5-423e-8a40-ac4803db8adc.jpg"
    }
}
