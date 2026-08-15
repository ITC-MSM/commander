package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Turtle Power!
 * {2}{G}
 * Enchantment
 *
 * Flash
 * Turtles you control get +2/+2.
 */
val TurtlePower = card("Turtle Power!") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Flash\nTurtles you control get +2/+2."

    keywords(Keyword.FLASH)

    staticAbility {
        ability = ModifyStats(
            2,
            2,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype("Turtle").youControl()),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "135"
        artist = "Hokyoung Kim"
        flavorText = "Unnoticed by the crowd, the strange canister bounced several more times, striking and smashing a glass jar which held four small turtles . . ."
        imageUri = "https://cards.scryfall.io/normal/front/4/0/4079ca90-dcc3-4184-9dfc-a626e0776332.jpg?1769006224"
    }
}
