package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Miner's Bane
 * {4}{R}{R}
 * Creature — Elemental
 * 6/3
 * {2}{R}: This creature gets +1/+0 and gains trample until end of turn.
 */
val MinersBane = card("Miner's Bane") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 6
    toughness = 3
    oracleText = "{2}{R}: This creature gets +1/+0 and gains trample until end of turn. (It can deal excess combat damage to the player or planeswalker it's attacking.)"

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
            .then(Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Adam Paquette"
        flavorText = "There are certain stones even dwarves know to leave in the earth."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c52d48ac-1685-4061-9907-31248ae38cc9.jpg?1783939170"
    }
}
