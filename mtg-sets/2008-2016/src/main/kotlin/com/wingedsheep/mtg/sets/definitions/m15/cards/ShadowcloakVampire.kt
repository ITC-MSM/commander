package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shadowcloak Vampire
 * {4}{B}
 * Creature — Vampire
 * 4/3
 * Pay 2 life: This creature gains flying until end of turn.
 */
val ShadowcloakVampire = card("Shadowcloak Vampire") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 4
    toughness = 3
    oracleText = "Pay 2 life: This creature gains flying until end of turn. (It can't be blocked except by creatures with flying or reach.)"

    activatedAbility {
        cost = Costs.PayLife(2)
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "113"
        artist = "Cynthia Sheppard"
        flavorText = "\"My favorite guilty pleasure? Are there innocent ones?\""
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b4911e9-01e6-4c41-9f2a-dfc25bedb2f7.jpg?1783939180"
    }
}
