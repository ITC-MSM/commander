package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Riveteers Initiate
 * {1}{R}
 * Creature — Lizard Citizen
 * 2 / 2
 * {1}{B/G}: This creature gains deathtouch until end of turn.
 */
val RiveteersInitiate = card("Riveteers Initiate") {
    manaCost = "{1}{R}"
    colorIdentity = "BGR"
    typeLine = "Creature — Lizard Citizen"
    oracleText = "{1}{B/G}: This creature gains deathtouch until end of turn."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{1}{B/G}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Svetlin Velinov"
        flavorText = "Young viashino flock to the Riveteers for Ziatora's leadership, eager to unleash their own inner dragons."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2e65a50-d2bc-43a0-a9d4-0e846d170f78.jpg?1783923114"
    }
}
