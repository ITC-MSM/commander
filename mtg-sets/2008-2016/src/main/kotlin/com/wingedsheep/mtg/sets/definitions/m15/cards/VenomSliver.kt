package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Venom Sliver
 * {1}{G}
 * Creature — Sliver
 * 1/1
 * Sliver creatures you control have deathtouch.
 */
val VenomSliver = card("Venom Sliver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "Sliver creatures you control have deathtouch. (Any amount of damage a creature with deathtouch deals to a creature is enough to destroy it.)"

    staticAbility {
        ability = GrantKeyword(
            Keyword.DEATHTOUCH,
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "205"
        artist = "Dave Kendall"
        flavorText = "\"We attacked with arrows dipped in poison. The slivers that did not die began to change.\"\n—Hastric, Thunian scout"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8db38bd9-bf58-41ca-84b9-f3582670143e.jpg?1783939160"
    }
}
