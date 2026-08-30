package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ukud Cobra
 * {3}{B}
 * Creature — Snake
 * 2 / 5
 *
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 *
 * One evergreen keyword and nothing else.
 */
val UkudCobra = card("Ukud Cobra") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Snake"
    power = 2
    toughness = 5
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "123"
        artist = "Johann Bodin"
        flavorText = "\"The Silumgar hide behind the deadly wildlife of their swamps. They'd rather scheme in their jungle palaces than face us.\"\n—Khibat, Kolaghan warrior"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71d2f6ee-af76-48f0-898d-3a19698d2790.jpg?1783938593"
    }
}
