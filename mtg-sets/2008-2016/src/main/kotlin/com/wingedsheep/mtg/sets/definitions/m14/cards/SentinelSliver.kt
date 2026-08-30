package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Sentinel Sliver
 * {1}{W}
 * Creature — Sliver
 * 2 / 2
 * Sliver creatures you control have vigilance. (Attacking doesn't cause them to tap.)
 */
val SentinelSliver = card("Sentinel Sliver") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control have vigilance. (Attacking doesn't cause them to tap.)"

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.VIGILANCE,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Maciej Kuciara"
        flavorText = "Through its watchful gaze, all slivers may see."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74c28560-e6ac-4be9-a253-22c4613b0d90.jpg"
    }
}
