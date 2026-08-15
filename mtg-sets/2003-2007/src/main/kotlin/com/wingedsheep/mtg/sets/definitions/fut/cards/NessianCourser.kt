package com.wingedsheep.mtg.sets.definitions.fut.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nessian Courser
 * {2}{G}
 * Creature — Centaur Warrior
 * 3/3
 *
 * Vanilla — no rules text.
 */
val NessianCourser = card("Nessian Courser") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Centaur Warrior"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Vance Kovacs"
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88b18b70-f656-4426-a3e5-69cf61fdaad1.jpg?1783943095"
    }
}
