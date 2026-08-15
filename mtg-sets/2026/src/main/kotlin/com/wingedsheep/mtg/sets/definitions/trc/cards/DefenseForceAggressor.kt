package com.wingedsheep.mtg.sets.definitions.trc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Defense Force Aggressor
 * {R}
 * Creature — Klingon Warrior
 * 2/1
 *
 * Vanilla — no rules text.
 */
val DefenseForceAggressor = card("Defense Force Aggressor") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Klingon Warrior"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "161"
        artist = "Josu Hernaiz"
        flavorText = "\"You play games with death, little warrior. I like it!\"\n—Captain Ma'ah"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67380a35-1a15-4c45-8a24-3c6077af85db.jpg?1785981114"
    }
}
