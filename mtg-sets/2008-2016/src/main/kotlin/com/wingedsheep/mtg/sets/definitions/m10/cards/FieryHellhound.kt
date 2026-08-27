package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fiery Hellhound
 * {1}{R}{R}
 * Creature — Elemental Dog
 * 2/2
 *
 * {R}: This creature gets +1/+0 until end of turn.
 *
 * A firebreathing pump — [Effects.ModifyStats] on [EffectTarget.Self] behind a [Costs.Mana].
 */
val FieryHellhound = card("Fiery Hellhound") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Dog"
    power = 2
    toughness = 2
    oracleText = "{R}: This creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Ted Galaday"
        flavorText = "Once a barbarian army has conquered a region, their shamans summon hellhounds that keep undesirables out and prisoners in."
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d6b2c8a-8019-4e4b-8f4e-058ab5284153.jpg?1783942374"
    }
}
