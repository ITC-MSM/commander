package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Concordant Crossroads
 * {G}
 * World Enchantment
 *
 * All creatures have haste.
 */
val ConcordantCrossroads = card("Concordant Crossroads") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "World Enchantment"
    oracleText = "All creatures have haste."

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, GroupFilter(GameObjectFilter.Creature))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "179"
        artist = "Amy Weber"
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3bdcfae4-86c9-4d8a-bcfe-f0a928ec29db.jpg?1783948049"
    }
}
