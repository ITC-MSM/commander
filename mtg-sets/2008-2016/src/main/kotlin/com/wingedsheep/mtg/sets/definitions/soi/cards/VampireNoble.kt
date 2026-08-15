package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vampire Noble
 * {2}{B}
 * Creature — Vampire Noble
 * 3/2
 *
 * Vanilla — no rules text.
 */
val VampireNoble = card("Vampire Noble") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Noble"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Ryan Alexander Lee"
        flavorText = "\"No emergency is so dire that it cannot be dealt with elegantly.\"\n—Olivia Voldaren"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2435f17-0378-4480-8d56-d256245c7ced.jpg?1783937761"
    }
}
