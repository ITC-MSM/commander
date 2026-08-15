package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Barktooth Warbeard
 * {4}{B}{R}{R}
 * Legendary Creature — Human Warrior
 * 6/5
 *
 * Vanilla — no rules text.
 */
val BarktoothWarbeard = card("Barktooth Warbeard") {
    manaCost = "{4}{B}{R}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Human Warrior"
    power = 6
    toughness = 5

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "221"
        artist = "Andi Rusu"
        flavorText = "He is devious and cunning, in both appearance and deed. Beware the Warbeard, for this brute bites as well as he barks!"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0ea52228-f8ad-4623-9e05-f162473bfc03.jpg?1783948041"
    }
}
