package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Giant Scorpion
 * {2}{B}
 * Creature — Scorpion
 * 1/3
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 */
val GiantScorpion = card("Giant Scorpion") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Scorpion"
    power = 1
    toughness = 3
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Raymond Swanland"
        flavorText = "Its sting hurts, but death is strangely painless."
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c27221df-ec7a-4c51-b3a8-34b65b236b49.jpg"
    }
}
