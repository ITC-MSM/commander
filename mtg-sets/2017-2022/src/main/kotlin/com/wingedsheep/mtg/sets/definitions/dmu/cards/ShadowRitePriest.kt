package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Shadow-Rite Priest
 * {1}{B}
 * Creature — Human Cleric
 * 2/2
 * Other Clerics you control get +1/+1.
 * {3}{B}{B}, {T}, Sacrifice another Cleric: Search your library for a black creature card, put it onto the battlefield, then shuffle.
 */
val ShadowRitePriest = card("Shadow-Rite Priest") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Cleric"
    oracleText = "Other Clerics you control get +1/+1.\n{3}{B}{B}, {T}, Sacrifice another Cleric: Search your library for a black creature card, put it onto the battlefield, then shuffle."
    power = 2
    toughness = 2

    // "Other Clerics you control" is every Cleric *permanent* you control minus this creature.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype("Cleric").youControl(),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}{B}{B}"),
            Costs.Tap,
            Costs.SacrificeAnother(GameObjectFilter.Permanent.withSubtype("Cleric"))
        )
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature.withColor(Color.BLACK),
            count = 1,
            destination = SearchDestination.BATTLEFIELD
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "106"
        artist = "Michael C. Hayes"
        flavorText = "Backstabbing is common within demonic cults. So are front- and side-stabbing."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9e43116-a489-4557-a47f-623d915a2b79.jpg?1783921326"
    }
}
