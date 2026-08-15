package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Giant Tortoise
 * {1}{U}
 * Creature — Turtle
 * 1/1
 * This creature gets +0/+3 as long as it's untapped.
 */
val GiantTortoise = card("Giant Tortoise") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Turtle"
    power = 1
    toughness = 1
    oracleText = "This creature gets +0/+3 as long as it's untapped."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(0, 3, Filters.Self),
            condition = Conditions.SourceIsUntapped,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Kaja Foglio"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/096f7ac8-c639-4347-9767-7305eaf490ba.jpg?1562896822"
    }
}
