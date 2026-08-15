package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * City of Brass
 * Land
 * Whenever this land becomes tapped, it deals 1 damage to you.
 * {T}: Add one mana of any color.
 */
val CityOfBrass = card("City of Brass") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "Whenever this land becomes tapped, it deals 1 damage to you.\n{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        effect = Effects.DealDamage(1, EffectTarget.Controller)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "71"
        artist = "Mark Tedin"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4e32327-380d-471e-813b-4c27477787ce.jpg?1562941005"
    }
}
