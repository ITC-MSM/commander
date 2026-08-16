package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Battlefly Swarm
 * {B}
 * Creature — Phyrexian Insect
 * 1/1
 * Flying
 * {B}: This creature gains deathtouch until end of turn.
 */
val BattleflySwarm = card("Battlefly Swarm") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Insect"
    oracleText = "Flying\n{B}: This creature gains deathtouch until end of turn."
    power = 1
    toughness = 1

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "Xavier Ribeiro"
        flavorText = "Having encountered the bitter Phyrexian battleflies before, Squee knew not to bother eating them—or at least to stop after the fifth."
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c6b4a2d-0bc0-4a54-9c78-712b48ad6be1.jpg?1783921337"
    }
}
