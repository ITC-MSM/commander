package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Caldera Lake
 * Land
 *
 * This land enters tapped.
 * {T}: Add {C}.
 * {T}: Add {U} or {R}. This land deals 1 damage to you.
 */
val CalderaLake = card("Caldera Lake") {
    typeLine = "Land"
    colorIdentity = "UR"
    oracleText = "This land enters tapped.\n{T}: Add {C}.\n{T}: Add {U} or {R}. This land deals 1 damage to you."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
            .then(Effects.DealDamage(1, EffectTarget.PlayerRef(Player.You)))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
            .then(Effects.DealDamage(1, EffectTarget.PlayerRef(Player.You)))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "316"
        artist = "Allen Williams"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f01fe22-e8ff-4106-8ac5-693ef920b2c9.jpg?1562054963"
    }
}
