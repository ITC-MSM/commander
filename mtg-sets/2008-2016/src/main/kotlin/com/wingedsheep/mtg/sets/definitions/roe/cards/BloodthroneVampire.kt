package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bloodthrone Vampire — Rise of the Eldrazi #98
 * {1}{B} · Creature — Vampire · 1 / 1
 *
 * Sacrifice a creature: This creature gets +2/+2 until end of turn.
 *
 * "Sacrifice a creature" accepts any creature you control, the Vampire included, so the cost is a
 * plain [Costs.Sacrifice] over [Filters.Creature] — not `SacrificeAnother`. Sacrificing the Vampire
 * to its own ability is a legal activation; the pump then has no permanent left to modify.
 */
val BloodthroneVampire = card("Bloodthrone Vampire") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 1
    toughness = 1
    oracleText = "Sacrifice a creature: This creature gets +2/+2 until end of turn."

    activatedAbility {
        cost = Costs.Sacrifice(Filters.Creature)
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Steve Argyle"
        flavorText = "Some humans willingly offered up their blood, hoping it would grant the vampire families the strength to stave off the Eldrazi."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48bf0233-1d2e-40cb-9a69-8eeeeb2959ca.jpg?1783941988"
    }
}
