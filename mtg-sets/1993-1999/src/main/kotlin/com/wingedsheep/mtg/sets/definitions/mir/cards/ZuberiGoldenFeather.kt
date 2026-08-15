package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Zuberi, Golden Feather
 * {4}{W}
 * Legendary Creature — Griffin
 * 3/3
 * Flying
 * Other Griffin creatures get +1/+1.
 */
val ZuberiGoldenFeather = card("Zuberi, Golden Feather") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Griffin"
    power = 3
    toughness = 3
    oracleText = "Flying\nOther Griffin creatures get +1/+1."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Griffin"), excludeSelf = true)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "51"
        artist = "Alan Rabinowitz"
        flavorText = "\"If the griffins tell of their gods, perhaps they speak of feathers bright as the Sun.\"\n—Afari, Tales"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1b24a80-b5e1-484f-9e21-886cb6b5db48.jpg?1562721448"
    }
}
