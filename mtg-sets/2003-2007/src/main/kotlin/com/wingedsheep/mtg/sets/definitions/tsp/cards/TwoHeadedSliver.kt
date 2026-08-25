package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Two-Headed Sliver
 * {1}{R}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures have menace. (They can't be blocked except by two or more creatures.)
 */
val TwoHeadedSliver = card("Two-Headed Sliver") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures have menace. (They can't be blocked except by two or more creatures.)"

    staticAbility {
        ability = GrantKeyword(
            Keyword.MENACE,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Dany Orizio"
        flavorText = "\"That which would be a fatal mutation in any other species is merely a source of new powers. I am intrigued, yet too fearful to examine it more closely.\"\n—Rukarumel, field journal"
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f89fb3b-0238-4d76-a46d-7d6fa4a74620.jpg"
    }
}
