package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nyxborn Seaguard
 * {2}{U}{U}
 * Enchantment Creature — Merfolk Soldier
 * 2/5
 *
 * Vanilla — no rules text.
 */
val NyxbornSeaguard = card("Nyxborn Seaguard") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Merfolk Soldier"
    power = 2
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Simon Dominic"
        flavorText = "\"Storm-tossed and broken, Callaphe cried out to deep-dwelling Thassa.\nTritons came swiftly to save her, bringing her north to the Lindus.\"\n—*The Callapheia*"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9ad0c7d7-0e44-496f-a2fc-fafc604cb1f1.jpg?1783931581"
    }
}
