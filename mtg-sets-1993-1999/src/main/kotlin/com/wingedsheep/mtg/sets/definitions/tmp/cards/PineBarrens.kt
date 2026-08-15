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
 * Pine Barrens
 * Land
 *
 * This land enters tapped.
 * {T}: Add {C}.
 * {T}: Add {B} or {G}. This land deals 1 damage to you.
 */
val PineBarrens = card("Pine Barrens") {
    typeLine = "Land"
    colorIdentity = "BG"
    oracleText = "This land enters tapped.\n{T}: Add {C}.\n{T}: Add {B} or {G}. This land deals 1 damage to you."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
            .then(Effects.DealDamage(1, EffectTarget.PlayerRef(Player.You)))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
            .then(Effects.DealDamage(1, EffectTarget.PlayerRef(Player.You)))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "321"
        artist = "Rebecca Guay"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5ac39e8-bd0e-4fa3-bc1e-a93944d013f3.jpg?1562056869"
    }
}
