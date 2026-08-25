package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Fury Sliver
 * {5}{R}
 * Creature — Sliver
 * 3/3
 * All Sliver creatures have double strike.
 */
val FurySliver = card("Fury Sliver") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 3
    toughness = 3
    oracleText = "All Sliver creatures have double strike."

    staticAbility {
        ability = GrantKeyword(
            Keyword.DOUBLE_STRIKE,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "157"
        artist = "Paolo Parente"
        flavorText = "\"A rift opened, and our arrows were abruptly stilled. To move was to push the world. But the sliver's claw still twitched, red wounds appeared in Thed's chest, and ribbons of blood hung in the air.\"\n—Adom Capashen, Benalish hero"
        imageUri = "https://cards.scryfall.io/normal/front/0/0/0000579f-7b35-4ed3-b44c-db2a538066fe.jpg"
    }
}
