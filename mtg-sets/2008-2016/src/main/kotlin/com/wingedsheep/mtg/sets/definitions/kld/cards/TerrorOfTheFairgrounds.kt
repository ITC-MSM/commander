package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Terror of the Fairgrounds
 * {3}{R}
 * Creature — Gremlin
 * 5/2
 *
 * Vanilla — no rules text.
 */
val TerrorOfTheFairgrounds = card("Terror of the Fairgrounds") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Gremlin"
    power = 5
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "137"
        artist = "Filip Burburan"
        flavorText = "\"Consider this a high alert, people. Permission to destroy on sight. Either we take it down or it'll take down the Fair.\"\n—Pav, gremlin watch"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04623df9-8fa9-44cc-b528-c2c484626d1f.jpg?1783937185"
    }
}
