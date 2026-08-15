package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pillarfield Ox
 * {3}{W}
 * Creature — Ox
 * 2/4
 *
 * Vanilla — no rules text.
 */
val PillarfieldOx = card("Pillarfield Ox") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Ox"
    power = 2
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Andrew Robinson"
        flavorText = "These stubborn and unpredictable oxen inspire the plains nomads' most colorful curses."
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d70a8ff1-f0cf-4aef-ad90-06902f98d434.jpg?1783942168"
    }
}
