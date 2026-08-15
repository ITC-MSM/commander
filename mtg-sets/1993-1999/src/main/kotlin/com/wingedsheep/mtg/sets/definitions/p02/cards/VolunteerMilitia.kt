package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Volunteer Militia
 * {W}
 * Creature — Human Soldier
 * 1/2
 *
 * Vanilla — no rules text.
 */
val VolunteerMilitia = card("Volunteer Militia") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Keith Parkinson"
        flavorText = "People fight hardest on their own soil."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de307b2e-9a1c-4f95-887f-14c9d99577aa.jpg?1783946489"
    }
}
