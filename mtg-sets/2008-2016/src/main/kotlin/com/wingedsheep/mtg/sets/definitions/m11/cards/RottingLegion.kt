package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Rotting Legion
 * {4}{B}
 * Creature — Zombie
 * 4/5
 *
 * This creature enters tapped.
 *
 * A bare [EntersTapped] self-replacement — the shape Shambling Cie'th uses.
 */
val RottingLegion = card("Rotting Legion") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 4
    toughness = 5
    oracleText = "This creature enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Carl Critchlow"
        flavorText = "Zombies have one speed: shamble."
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bfe5e62c-83ce-4c2b-a745-acd7670e115d.jpg?1783941812"
    }
}
