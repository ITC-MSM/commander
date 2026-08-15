package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Firescreamer
 * {3}{B}
 * Creature — Kavu
 * 2/2
 * {R}: This creature gets +1/+0 until end of turn.
 */
val Firescreamer = card("Firescreamer") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Kavu"
    power = 2
    toughness = 2
    oracleText = "{R}: This creature gets +1/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{R}: This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Alan Pollack"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/155a2213-bf6e-4a54-924b-e450b7d06f26.jpg?1562899300"
    }
}
