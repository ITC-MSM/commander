package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Caterwauling Boggart
 * {3}{R}
 * Creature — Goblin Shaman
 * 2/2
 * Goblins you control and Elementals you control have menace.
 */
val CaterwaulingBoggart = card("Caterwauling Boggart") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 2
    oracleText = "Goblins you control and Elementals you control have menace. (They can't be blocked except " +
        "by two or more creatures.)"

    staticAbility {
        // "Goblins you control" is every Goblin permanent, not only creatures — and it includes the Boggart itself.
        ability = GrantKeyword(
            Keyword.MENACE,
            GroupFilter(GameObjectFilter.Permanent.withAnySubtype("Goblin", "Elemental").youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Steven Belledin"
        flavorText = "\"As far as I can tell, that frog dangling from the stick serves absolutely no purpose " +
            "whatsoever.\"\n—Gaddock Teeg"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee8337bf-9fe8-45a0-974c-8911306b19ea.jpg?1783942880"
    }
}
