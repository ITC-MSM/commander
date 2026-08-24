package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Emmessi Tome
 * {4}
 * Artifact — Book
 * {5}, {T}: Draw two cards, then discard a card.
 */
val EmmessiTome = card("Emmessi Tome") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact — Book"
    oracleText = "{5}, {T}: Draw two cards, then discard a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap)
        effect = Patterns.Hand.loot(draw = 2, discard = 1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "284"
        artist = "Tom Wänerstrand"
        flavorText = "It is like life and destiny: we think we know the story, but we have read only half the tale."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a870e48a-41ae-4d9f-b181-074deb067d40.jpg"
    }
}
