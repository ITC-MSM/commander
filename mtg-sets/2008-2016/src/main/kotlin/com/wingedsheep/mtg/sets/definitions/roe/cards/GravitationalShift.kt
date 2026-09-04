package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gravitational Shift
 * {3}{U}{U}
 * Enchantment
 *
 * Creatures with flying get +2/+0.
 * Creatures without flying get -2/-0.
 *
 * Modeling notes:
 *  - Two printed sentences, two `staticAbility { }` blocks. One `ModifyStats` carries one
 *    (bonus, filter) pair, so the +2/+0 half and the -2/-0 half cannot share a block.
 *  - The two halves partition all creatures by the FLYING keyword:
 *    `GameObjectFilter.Creature.withKeyword(FLYING)` and `.withoutKeyword(FLYING)`. Both read
 *    projected keywords, so a creature that gains or loses flying mid-turn moves between the two
 *    groups on its own — which is what makes this one enchantment rather than two snapshots.
 *  - Neither sentence says "you control", so neither filter gets a `youControl()`: this is a
 *    symmetric global effect that shrinks the opponent's ground creatures too (the whole point
 *    of the card in a blue flyers deck). Contrast Favorable Winds, whose printed "you control"
 *    does scope its filter.
 *  - The negative half is `powerBonus = -2, toughnessBonus = 0` — "-2/-0" touches power only.
 */
val GravitationalShift = card("Gravitational Shift") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Creatures with flying get +2/+0.\n" +
            "Creatures without flying get -2/-0."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
        )
    }

    staticAbility {
        ability = ModifyStats(
            powerBonus = -2,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "69"
        artist = "Svetlin Velinov"
        flavorText = "As they awakened, the Eldrazi reasserted their mastery over all of Zendikar's natural forces."
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bad32b9f-0aa4-4036-90e6-c087cffd52e7.jpg?1783941996"
    }
}
