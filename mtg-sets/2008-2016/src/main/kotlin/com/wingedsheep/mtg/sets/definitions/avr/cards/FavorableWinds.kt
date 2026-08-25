package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Favorable Winds
 * {1}{U}
 * Enchantment
 *
 * Creatures you control with flying get +1/+1.
 */
val FavorableWinds = card("Favorable Winds") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Creatures you control with flying get +1/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "51"
        artist = "Winona Nelson"
        flavorText = "Long thought to be extinct, flocks of gryffs reappeared with Avacyn's return."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4cbd57f1-9883-40a4-9b52-1649cee83815.jpg"
    }
}
