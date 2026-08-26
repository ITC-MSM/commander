package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Greater Basilisk
 * {3}{G}{G}
 * Creature — Basilisk
 * 3/5
 *
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 */
val GreaterBasilisk = card("Greater Basilisk") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Basilisk"
    power = 3
    toughness = 5
    oracleText = "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "James Ryman"
        flavorText = "Bone, stone . . . both taste the same to a hungry basilisk."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/482f169d-8acd-4ee3-a54c-6df6cbeb7eca.jpg?1783941796"
    }
}
