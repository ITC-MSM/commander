package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tower Drake
 * {2}{U}
 * Creature — Drake
 * 2/1
 * Flying
 * {W}: This creature gets +0/+1 until end of turn.
 */
val TowerDrake = card("Tower Drake") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 2
    toughness = 1
    oracleText = "Flying\n{W}: This creature gets +0/+1 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = ModifyStatsEffect(0, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aef97b38-f7a5-4db7-9550-24aa1a1ebbda.jpg?1562930214"
    }
}
