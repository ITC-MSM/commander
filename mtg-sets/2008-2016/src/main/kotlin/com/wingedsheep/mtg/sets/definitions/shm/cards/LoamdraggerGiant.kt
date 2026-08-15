package com.wingedsheep.mtg.sets.definitions.shm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Loamdragger Giant
 * {4}{R/G}{R/G}{R/G}
 * Creature — Giant Warrior
 * 7/6
 *
 * Vanilla — no rules text.
 */
val LoamdraggerGiant = card("Loamdragger Giant") {
    manaCost = "{4}{R/G}{R/G}{R/G}"
    colorIdentity = "RG"
    typeLine = "Creature — Giant Warrior"
    power = 7
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Pete Venters"
        flavorText = "Giants sleep soundly and long, sometimes for long enough that a crust of earth and moss grows over them. But inevitably something disturbs their slumber, and they wake unhappy."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a27bbe4-5341-4b2b-9ae8-eb56585a9c3a.jpg?1783942721"
    }
}
