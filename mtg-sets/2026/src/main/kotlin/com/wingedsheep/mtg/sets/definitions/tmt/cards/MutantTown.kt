package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Mutant Town
 * Land
 *
 * This land enters tapped.
 * When this land enters, you gain 1 life.
 * {T}: Add {G} or {U}.
 */
val MutantTown = card("Mutant Town") {
    typeLine = "Land"
    colorIdentity = "GU"
    oracleText = "This land enters tapped.\nWhen this land enters, you gain 1 life.\n{T}: Add {G} or {U}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(1)
        description = "When this land enters, you gain 1 life."
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
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Josu Solano"
        flavorText = "\"Until we completely understand the mutagenic agent's effects, these people must remain contained for our health and safety.\"\n—Baxter Stockman"
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6eac43d-08b6-45a4-803b-10a321a241d7.jpg?1771587105"
    }
}
