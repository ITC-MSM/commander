package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Builder's Blessing
 * {3}{W}
 * Enchantment
 *
 * Untapped creatures you control get +0/+2.
 *
 * A lord whose group filter carries a *state* predicate: `untapped()` puts
 * [com.wingedsheep.sdk.scripting.predicates.StatePredicate.IsUntapped] on the filter, so the
 * bonus is re-read every projection — a creature loses the +0/+2 the moment it attacks or taps for
 * an ability, and gets it back on untap. That live re-evaluation is why this is a static
 * [ModifyStats] over a filter rather than a one-shot pump.
 */
val BuildersBlessing = card("Builder's Blessing") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Untapped creatures you control get +0/+2."

    staticAbility {
        ability = ModifyStats(
            0, 2,
            GroupFilter(GameObjectFilter.Creature.untapped().youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "John Stanko"
        flavorText = "\"Mix the mortar with holy wards, or blood will run in the streets.\"\n—Vadvar, Thraben stonewright"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2ad27af1-b482-40d5-9dbb-11201ffa0410.jpg?1783940741"
    }
}
