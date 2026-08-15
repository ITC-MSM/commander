package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Manta Riders
 * {U}
 * Creature — Merfolk
 * 1/1
 * {U}: This creature gains flying until end of turn.
 */
val MantaRiders = card("Manta Riders") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk"
    power = 1
    toughness = 1
    oracleText = "{U}: This creature gains flying until end of turn."

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Kaja Foglio"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdff306c-1c7e-49ae-b10f-99e1927bbef1.jpg?1562056858"
    }
}
