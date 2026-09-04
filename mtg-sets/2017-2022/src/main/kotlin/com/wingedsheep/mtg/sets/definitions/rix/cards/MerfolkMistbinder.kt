package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Merfolk Mistbinder
 * {G}{U}
 * Creature — Merfolk Shaman
 * 2/2
 * Other Merfolk you control get +1/+1.
 *
 * A Merfolk lord; see [LegionLieutenant] for why the filter is `Permanent`, not `Creature`.
 */
val MerfolkMistbinder = card("Merfolk Mistbinder") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Merfolk Shaman"
    oracleText = "Other Merfolk you control get +1/+1."
    power = 2
    toughness = 2

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "164"
        artist = "Clint Cearley"
        flavorText = "\"The mist clothes us when we are bare, hides us when we are alone, and " +
            "unites us when we are together.\"\n—Nirit of Pashona's band"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/2935b829-23fb-415e-90f2-2e0016b5cde9.jpg?1783935271"
        ruling(
            "2018-01-19",
            "Because damage remains marked on a creature until it's removed as the turn ends, " +
                "nonlethal damage dealt to another Merfolk you control may become lethal if " +
                "Merfolk Mistbinder leaves the battlefield during that turn."
        )
    }
}
