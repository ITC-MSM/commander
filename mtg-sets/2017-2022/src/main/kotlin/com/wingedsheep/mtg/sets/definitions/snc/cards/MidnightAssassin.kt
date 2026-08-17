package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Midnight Assassin
 * {2}{B}
 * Creature — Vampire Assassin
 * 1 / 2
 * Flying
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 */
val MidnightAssassin = card("Midnight Assassin") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Assassin"
    oracleText = "Flying\nDeathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"
    power = 1
    toughness = 2

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Christina Davis"
        flavorText = "The broad avenues and graceful spires of Park Heights are the playground of the affluent. After nightfall, they also make a fine hunting ground."
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02a4a5b3-0477-4709-8bce-3e01f54001b6.jpg?1783923128"
    }
}
