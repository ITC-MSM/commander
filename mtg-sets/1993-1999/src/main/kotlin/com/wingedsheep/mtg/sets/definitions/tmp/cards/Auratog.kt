package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Auratog
 * {1}{W}
 * Creature — Atog
 * 1/2
 * Sacrifice an enchantment: This creature gets +2/+2 until end of turn.
 */
val Auratog = card("Auratog") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Atog"
    power = 1
    toughness = 2
    oracleText = "Sacrifice an enchantment: This creature gets +2/+2 until end of turn."

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Enchantment)
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Jeff Miracola"
        flavorText = "The auratog enjoys eating its wards."
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86dca066-d5e3-442a-95a0-e695c1d5850c.jpg"
    }
}
