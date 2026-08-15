package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Murasa Brute
 * {2}{G}
 * Creature — Troll Warrior
 * 3/3
 *
 * Vanilla — no rules text.
 */
val MurasaBrute = card("Murasa Brute") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Troll Warrior"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "195"
        artist = "Caio Monteiro"
        flavorText = "Some adventuring parties are made up of old friends, their common bonds bolstering them in the face of peril. Other parties simply hire the biggest, meanest muscle they can find."
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efe1c5b2-4356-41ae-ab7e-ad9fc835a911.jpg?1783929336"
    }
}
