package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ancient Kavu
 * {3}{R}
 * Creature — Kavu
 * 3/3
 * {2}: This creature becomes colorless until end of turn.
 */
val AncientKavu = card("Ancient Kavu") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu"
    power = 3
    toughness = 3
    oracleText = "{2}: This creature becomes colorless until end of turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.ChangeColor(EffectTarget.Self, colors = emptySet())
        description = "{2}: This creature becomes colorless until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Glen Angus"
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c8ccb5d0-735b-443f-addd-8b70f5f2c60d.jpg?1562935287"
    }
}
