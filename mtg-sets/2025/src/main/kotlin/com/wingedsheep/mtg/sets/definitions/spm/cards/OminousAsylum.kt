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
 * Ominous Asylum
 * Land
 * This land enters tapped.
 * {T}: Add {B} or {R}.
 * {4}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 */
val OminousAsylum = card("Ominous Asylum") {
    manaCost = ""
    colorIdentity = "BR"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {B} or {R}.\n{4}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"
    replacementEffect(EntersTapped())
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
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Patterns.Library.surveil(1)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "181"
        artist = "Pavel Kolomeyets"
        flavorText = "\"We believe each patient has their own unique path to healing.\"\n—Dr. Ashley Kafka, Ravencroft Institute"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/4329f94a-9110-4f07-b4a6-f1ccae97ccc9.jpg?1757378149"
    }
}
