package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan

/**
 * Ironhoof Ox
 * {3}{G}{G}
 * Creature — Ox
 * 4/4
 *
 * This creature can't be blocked by more than one creature.
 */
val IronhoofOx = card("Ironhoof Ox") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ox"
    oracleText = "This creature can't be blocked by more than one creature."
    power = 4
    toughness = 4

    staticAbility {
        ability = CantBeBlockedByMoreThan(maxBlockers = 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "130"
        artist = "Una Fricker"
        flavorText = "The good news is it's vegetarian. The bad news is it just doesn't like you."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c2528c2-6405-4db9-9137-623946a4de2f.jpg"
    }
}
