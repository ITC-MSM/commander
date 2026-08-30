package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Syphon Sliver
 * {2}{B}
 * Creature — Sliver
 * 2 / 2
 * Sliver creatures you control have lifelink. (Damage dealt by a Sliver creature you control also causes you to gain that much life.)
 */
val SyphonSliver = card("Syphon Sliver") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control have lifelink. (Damage dealt by a Sliver creature you control also causes you to gain that much life.)"

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.LIFELINK,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "117"
        artist = "Tyler Jacobson"
        flavorText = "When the hive must feed, every appendage becomes an additional mouth."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85cb40e3-c3ed-4b3f-88ad-6f1305297c6f.jpg"
    }
}
