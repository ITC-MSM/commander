package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Phyrexian Infiltrator
 * {2}{B}
 * Creature — Phyrexian Minion
 * 2/2
 * {2}{U}{U}: Exchange control of this creature and target creature. (This effect lasts indefinitely.)
 */
val PhyrexianInfiltrator = card("Phyrexian Infiltrator") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Minion"
    power = 2
    toughness = 2
    oracleText = "{2}{U}{U}: Exchange control of this creature and target creature. (This effect lasts indefinitely.)"

    activatedAbility {
        cost = Costs.Mana("{2}{U}{U}")
        val t = target("target", Targets.Creature)
        effect = Effects.ExchangeControl(EffectTarget.Self, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "116"
        artist = "Darrell Riche"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/224b8254-553d-4d88-8163-1f15e1244bd2.jpg?1562901847"
    }
}
