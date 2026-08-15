package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zombie Goliath
 * {4}{B}
 * Creature — Zombie Giant
 * 4/3
 *
 * Vanilla — no rules text.
 */
val ZombieGoliath = card("Zombie Goliath") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Giant"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "E. M. Gist"
        flavorText = "\"Phirax of Blood Ridge has sent a war giant at us? What, do I have to spell it out for you? Kill the giant, scoop out its skull, and drive it back to Blood Ridge. Honestly, what kind of necromancer minions are you?\"\n—Keren-Dur, necromancer lord"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc295834-af33-45ae-be4d-7a1987f85561.jpg?1783942376"
    }
}
