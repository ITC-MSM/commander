package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Zof Shade
 * {3}{B}
 * Creature — Shade
 * 2/2
 * {2}{B}: This creature gets +2/+2 until end of turn.
 *
 * Canonical printing: Rise of the Eldrazi, the card's earliest real-expansion printing. Reprinted
 * in M15 as a `Printing` row.
 */
val ZofShade = card("Zof Shade") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade"
    power = 2
    toughness = 2
    oracleText = "{2}{B}: This creature gets +2/+2 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}{B}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "132"
        artist = "Jason A. Engle"
        flavorText = "It haunts the remnants of the Helix of Zof, leeching strength from the vast Eldrazi ruin."
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b46976d4-7266-4359-bf7a-7e81983ae6e3.jpg?1783941979"
    }
}
