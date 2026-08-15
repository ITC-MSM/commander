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
 * Suburban Sanctuary
 * Land
 * This land enters tapped.
 * {T}: Add {G} or {W}.
 * {4}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 */
val SuburbanSanctuary = card("Suburban Sanctuary") {
    manaCost = ""
    colorIdentity = "WG"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {G} or {W}.\n{4}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"
    replacementEffect(EntersTapped())
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
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Patterns.Library.surveil(1)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "David Frasheski"
        flavorText = "\"You are always welcome here, Peter.\"\n—Aunt May"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/467df77a-a99c-4cfd-9af4-502eaa2eb2e3.jpg?1757378190"
    }
}
