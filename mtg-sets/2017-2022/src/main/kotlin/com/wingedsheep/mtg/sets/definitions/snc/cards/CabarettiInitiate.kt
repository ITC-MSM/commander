package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cabaretti Initiate
 * {G}
 * Creature — Raccoon Citizen
 * 1 / 2
 * {2}{R/W}: This creature gains double strike until end of turn.
 */
val CabarettiInitiate = card("Cabaretti Initiate") {
    manaCost = "{G}"
    colorIdentity = "GRW"
    typeLine = "Creature — Raccoon Citizen"
    oracleText = "{2}{R/W}: This creature gains double strike until end of turn."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{2}{R/W}")
        effect = Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Jason A. Engle"
        flavorText = "\"Now there's a fellow who knows how to have a good time!\"\n—Jetmir"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c691f62-009b-4178-8e8b-d6e88229a282.jpg?1783923106"
    }
}
