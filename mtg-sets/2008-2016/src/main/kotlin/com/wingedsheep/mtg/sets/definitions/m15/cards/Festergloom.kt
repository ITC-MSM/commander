package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Festergloom
 * {2}{B}
 * Sorcery
 * Nonblack creatures get -1/-1 until end of turn.
 *
 * A group pump over every nonblack creature — both players', so the group filter carries no
 * controller predicate.
 */
val Festergloom = card("Festergloom") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Nonblack creatures get -1/-1 until end of turn."

    spell {
        effect = Patterns.Group.modifyStatsForAll(
            -1, -1,
            GroupFilter(GameObjectFilter.Creature.notColor(Color.BLACK))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "97"
        artist = "Mathias Kollros"
        flavorText = "The death of a scout can be as informative as a safe return."
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3125137-bd18-488e-b45e-6fc23828c5bd.jpg?1783939184"
    }
}
