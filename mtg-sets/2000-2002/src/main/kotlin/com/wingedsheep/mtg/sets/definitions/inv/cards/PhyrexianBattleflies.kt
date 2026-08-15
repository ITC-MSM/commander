package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Phyrexian Battleflies
 * {B}
 * Creature — Phyrexian Insect
 * 0/1
 * Flying
 * {B}: This creature gets +1/+0 until end of turn. Activate no more than twice each turn.
 */
val PhyrexianBattleflies = card("Phyrexian Battleflies") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Insect"
    oracleText = "Flying\n{B}: This creature gets +1/+0 until end of turn. Activate no more than twice each turn."
    power = 0
    toughness = 1

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.MaxPerTurn(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Dan Frazier"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da27c489-c541-4b0d-a844-71aa65e55ceb.jpg?1562938879"
    }
}
