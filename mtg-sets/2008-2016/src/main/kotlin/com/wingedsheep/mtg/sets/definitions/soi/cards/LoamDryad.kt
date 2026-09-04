package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Loam Dryad (Shadows over Innistrad #216)
 * {G}
 * Creature — Dryad Horror
 * 1 / 2
 *
 * {T}, Tap an untapped creature you control: Add one mana of any color.
 *
 * A mana ability: it produces mana and takes no target, so `manaAbility = true` — which derives
 * the timing rule; there is no separate `timing` to author. The tap-a-creature half is a cost,
 * not the `{T}` symbol, so the creature tapped that way needn't be free of summoning sickness.
 */
val LoamDryad = card("Loam Dryad") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dryad Horror"
    power = 1
    toughness = 2
    oracleText = "{T}, Tap an untapped creature you control: Add one mana of any color."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.TapPermanents(1, GameObjectFilter.Creature))
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "Jose Cabrera"
        flavorText = "\"I've never known dryads to suffer visitors in their woods. Beware the Ulvenwald when she welcomes you.\"\n—Alena, trapper of Kessig"
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61c9441d-18d9-4ec6-859e-e9a7893b54e3.jpg?1783937727"
    }
}
