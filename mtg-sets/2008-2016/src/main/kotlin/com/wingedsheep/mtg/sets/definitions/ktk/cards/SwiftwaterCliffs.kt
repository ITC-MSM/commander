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
 * Swiftwater Cliffs
 * Land
 * This land enters tapped.
 * When this land enters, you gain 1 life.
 * {T}: Add {U} or {R}.
 */
val SwiftwaterCliffs = card("Swiftwater Cliffs") {
    typeLine = "Land"
    colorIdentity = "UR"
    oracleText = "This land enters tapped.\nWhen this land enters, you gain 1 life.\n{T}: Add {U} or {R}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(1)
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "243"
        artist = "Eytan Zana"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e782d005-a563-4738-978a-73a3465de78f.jpg?1562795257"
    }
}
