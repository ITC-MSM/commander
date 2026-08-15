package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Oasis
 * Land
 * {T}: Prevent the next 1 damage that would be dealt to target creature this turn.
 */
val Oasis = card("Oasis") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Prevent the next 1 damage that would be dealt to target creature this turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.PreventNextDamage(1, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "78"
        artist = "Brian Snõddy"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f38565e-88b9-433d-b0e9-a3b9734f183f.jpg?1562915597"
    }
}
