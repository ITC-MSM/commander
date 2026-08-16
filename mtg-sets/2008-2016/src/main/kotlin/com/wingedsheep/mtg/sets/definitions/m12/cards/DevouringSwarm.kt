package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Devouring Swarm
 * {1}{B}{B}
 * Creature — Insect
 * 2/1
 * Flying
 * Sacrifice a creature: This creature gets +1/+1 until end of turn.
 *
 * "Sacrifice a creature" accepts any creature you control, including this one, so the cost is
 * [Costs.Sacrifice] over [Filters.Creature] — not `SacrificeAnother`.
 */
val DevouringSwarm = card("Devouring Swarm") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    power = 2
    toughness = 1
    oracleText = "Flying\nSacrifice a creature: This creature gets +1/+1 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Sacrifice(Filters.Creature)
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Wayne England"
        flavorText = "Their arrival is heralded by the deafening hum of ten thousand wings. Their departure is marked by the silence of the dead."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/735c2c79-9b4f-4f86-9dec-0749237fe9ce.jpg?1783941082"
    }
}
