package com.wingedsheep.mtg.sets.definitions.bng.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cyclops of One-Eyed Pass
 * {2}{R}{R}
 * Creature — Cyclops
 * 5/2
 *
 * Vanilla — no rules text.
 */
val CyclopsOfOneEyedPass = card("Cyclops of One-Eyed Pass") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cyclops"
    power = 5
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Kev Walker"
        flavorText = "The Champion armed herself to face the cyclops, heedless of her companions' despair. \"How will you defeat it with only one spear?\" asked young Althemone. The Champion raised her weapon. \"It has but one eye.\"\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71a25c69-8e57-4a44-955a-da1541bbe0fe.jpg?1783939550"
    }
}
