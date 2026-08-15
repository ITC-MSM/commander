package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Centaur Courser
 * {2}{G}
 * Creature — Centaur Warrior
 * 3/3
 *
 * Vanilla — no rules text.
 */
val CentaurCourser = card("Centaur Courser") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Centaur Warrior"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "172"
        artist = "Vance Kovacs"
        flavorText = "\"The centaurs are truly free. Never will they be tamed by temptation or controlled by fear. They live in total harmony, a feat not yet achieved by our kind.\"\n—Ramal, sage of Westgate"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03354b67-7df2-4b4b-a996-a37550e58561.jpg?1783942365"
    }
}
