package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bonebreaker Giant
 * {4}{R}
 * Creature — Giant
 * 4/4
 *
 * Vanilla — no rules text.
 */
val BonebreakerGiant = card("Bonebreaker Giant") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Kev Walker"
        flavorText = "One thing's for sure—his fists are harder than your skull."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc17e5c1-a6b4-401b-95eb-1c01cd1da570.jpg?1783941073"
    }
}
