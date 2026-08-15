package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elite Vanguard
 * {W}
 * Creature — Human Soldier
 * 2/1
 *
 * Vanilla — no rules text.
 */
val EliteVanguard = card("Elite Vanguard") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Mark Tedin"
        flavorText = "The vanguard is skilled at waging war alone. The enemy is often defeated before its reinforcements reach the front."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6bda0b4b-ab5a-4d91-9dd1-7a5a145b67f5.jpg?1783942403"
    }
}
