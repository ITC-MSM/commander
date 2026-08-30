package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Galerider Sliver
 * {U}
 * Creature — Sliver
 * 1 / 1
 * Sliver creatures you control have flying.
 */
val GaleriderSliver = card("Galerider Sliver") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "Sliver creatures you control have flying."

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.FLYING,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "57"
        artist = "James Zapata"
        flavorText = "Masters of adaptation, galeriders serve multiple purposes useful to the hive. When they're not patrolling their territories, their majestic wings serve to circulate cool air through the vast hive chambers."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/425f5d1b-9989-4fd1-88e2-6c3108aefa0b.jpg"
    }
}
