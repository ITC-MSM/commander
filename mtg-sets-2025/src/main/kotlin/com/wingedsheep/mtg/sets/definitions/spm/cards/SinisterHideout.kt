package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Sinister Hideout
 * Land
 * This land enters tapped.
 * {T}: Add {U} or {B}.
 * {4}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 */
val SinisterHideout = card("Sinister Hideout") {
    manaCost = ""
    colorIdentity = "UB"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {U} or {B}.\n{4}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"
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
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Patterns.Library.surveil(1)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "184"
        artist = "Pavel Kolomeyets"
        flavorText = "Under the sea it stands the hidden headquarters of the Master Planner and his band of strangely-garbed criminals."
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23190d7e-5165-49bd-b307-bf81877d228d.jpg?1757378180"
    }
}
