package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Urborg Shambler
 * {2}{B}{B}
 * Creature — Horror
 * 4/3
 *
 * Other black creatures get -1/-1.
 */
val UrborgShambler = card("Urborg Shambler") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 4
    toughness = 3
    oracleText = "Other black creatures get -1/-1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = -1,
            toughnessBonus = -1,
            filter = GroupFilter(GameObjectFilter.Creature.withColor(Color.BLACK), excludeSelf = true)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "133"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eaedd5c8-03c6-4bbb-bf83-632551830bd4.jpg?1562942218"
    }
}
