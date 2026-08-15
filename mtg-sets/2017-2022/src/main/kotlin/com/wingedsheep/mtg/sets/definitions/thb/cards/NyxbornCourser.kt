package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nyxborn Courser
 * {1}{W}{W}
 * Enchantment Creature — Centaur Scout
 * 2/4
 *
 * Vanilla — no rules text.
 */
val NyxbornCourser = card("Nyxborn Courser") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Centaur Scout"
    power = 2
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Bastien L. Deharme"
        flavorText = "\"Storms drove them westward to Ketaphos; wide plains shimmered in starlight.\nCentaurs greeted them, offering gold-hued apples and grain-cakes.\"\n—*The Callapheia*"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fd32240-c003-4e18-adf1-e2e992c702b1.jpg?1783931593"
    }
}
