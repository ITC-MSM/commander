package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ferrovore
 * {2}{R}
 * Creature — Beast
 * 2/2
 *
 * {R}, Sacrifice an artifact: This creature gets +3/+0 until end of turn.
 *
 * The sacrifice is any artifact, not only one you control by some narrower reading — the cost's
 * filter is the bare artifact type, and the payer can only ever sacrifice permanents they control
 * anyway (CR 701.17a).
 */
val Ferrovore = card("Ferrovore") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 2
    oracleText = "{R}, Sacrifice an artifact: This creature gets +3/+0 until end of turn."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{R}"),
            Costs.Sacrifice(GameObjectFilter.Artifact)
        )
        effect = Effects.ModifyStats(3, 0, EffectTarget.Self)
        description = "{R}, Sacrifice an artifact: This creature gets +3/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Austin Hsu"
        flavorText = "The Vulshok use its digestion to break down the most obstinate artifacts, from darksteel myr to seastrider plates."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8dcc7170-38d9-4b9e-a5f9-73ac1208c439.jpg?1783941726"
    }
}
