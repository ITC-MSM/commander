package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fetid Imp
 * {1}{B}
 * Creature — Imp
 * 1/2
 * Flying
 * {B}: This creature gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy it.)
 */
val FetidImp = card("Fetid Imp") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Imp"
    power = 1
    toughness = 2
    oracleText = "Flying\n{B}: This creature gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy it.)"

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Nils Hamm"
        imageUri = "https://cards.scryfall.io/normal/front/8/9/891718e9-bef3-470a-80c5-514f7f43abe8.jpg?1783938342"
    }
}
