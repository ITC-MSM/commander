package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Groundshaker Sliver
 * {6}{G}
 * Creature — Sliver
 * 5 / 5
 * Sliver creatures you control have trample. (A creature with trample can deal excess combat damage to the player or planeswalker it's attacking.)
 */
val GroundshakerSliver = card("Groundshaker Sliver") {
    manaCost = "{6}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 5
    toughness = 5
    oracleText = "Sliver creatures you control have trample. (A creature with trample can deal excess combat damage to the player or planeswalker it's attacking.)"

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.TRAMPLE,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Chase Stone"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/712f0ce4-9189-4c75-9c2b-d370bce89052.jpg"
    }
}
