package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Spitting Sliver
 * {4}{B}
 * Creature — Sliver
 * 3/3
 * All Sliver creatures have first strike.
 */
val SpittingSliver = card("Spitting Sliver") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Sliver"
    power = 3
    toughness = 3
    oracleText = "All Sliver creatures have first strike."

    staticAbility {
        ability = GrantKeyword(
            Keyword.FIRST_STRIKE,
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Steve Ellis"
        flavorText = "\"Our lances' reach had been our saving grace in our fight against the hive. Now even that advantage is taken from us.\"\n—Adom Capashen, Benalish knight"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd07649e-c7fc-44f7-ab23-0fb935aff8c7.jpg"
    }
}
