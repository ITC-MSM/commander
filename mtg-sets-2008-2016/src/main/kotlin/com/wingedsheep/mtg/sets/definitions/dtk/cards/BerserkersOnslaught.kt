package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Berserkers' Onslaught
 * {3}{R}{R}
 * Enchantment
 * Attacking creatures you control have double strike.
 */
val BerserkersOnslaught = card("Berserkers' Onslaught") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Attacking creatures you control have double strike."

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.DOUBLE_STRIKE,
            filter = GroupFilter(GameObjectFilter.Creature.attacking().youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "130"
        artist = "Zoltan Boros"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee571a78-16b2-40ba-92b2-87c1bf4b39bc.jpg?1562795277"
    }
}
