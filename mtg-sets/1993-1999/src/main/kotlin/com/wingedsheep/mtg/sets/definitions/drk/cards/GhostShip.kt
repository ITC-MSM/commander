package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ghost Ship
 * {2}{U}{U}
 * Creature — Spirit
 * 2/4
 * Flying
 * {U}{U}{U}: Regenerate this creature.
 */
val GhostShip = card("Ghost Ship") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 4
    oracleText = "Flying\n{U}{U}{U}: Regenerate this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{U}{U}{U}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Tom Wänerstrand"
        flavorText = "\"That phantom prow split the storm as lightning cast its long shadow on the battlefield below.\" —Mireille Gaetane, *The Valeriad*"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db591b28-37e5-4e7c-ae4d-d761262b12d0.jpg?1783947943"
    }
}
