package com.wingedsheep.mtg.sets.definitions.ktk.cards

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
 * Tranquil Cove
 * Land
 * This land enters tapped.
 * When this land enters, you gain 1 life.
 * {T}: Add {W} or {U}.
 */
val TranquilCove = card("Tranquil Cove") {
    typeLine = "Land"
    colorIdentity = "WU"
    oracleText = "This land enters tapped.\nWhen this land enters, you gain 1 life.\n{T}: Add {W} or {U}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(1)
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
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
        collectorNumber = "246"
        artist = "John Avon"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f840bd2-c4f5-4ac4-918c-91b4feeb8783.jpg?1562782605"
    }
}
