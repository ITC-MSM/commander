package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Straw Soldiers
 * {1}{U}
 * Creature — Scarecrow Soldier
 * 1/3
 *
 * Vanilla — no rules text.
 */
val StrawSoldiers = card("Straw Soldiers") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Scarecrow Soldier"
    power = 1
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Cai Tingting"
        flavorText = "The overnight appearance of miles of \"armed\" Wu forts at Guangling frightened a much vaster Wei force into fleeing for their lives."
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8ae5ba21-eb8e-4663-bfd8-3e19a0c10774.jpg?1783946120"
    }
}
