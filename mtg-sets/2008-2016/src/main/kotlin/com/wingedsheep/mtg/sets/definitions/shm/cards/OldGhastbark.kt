package com.wingedsheep.mtg.sets.definitions.shm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Old Ghastbark
 * {3}{G/W}{G/W}
 * Creature — Treefolk Warrior
 * 3/6
 *
 * Vanilla — no rules text.
 */
val OldGhastbark = card("Old Ghastbark") {
    manaCost = "{3}{G/W}{G/W}"
    colorIdentity = "WG"
    typeLine = "Creature — Treefolk Warrior"
    power = 3
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "232"
        artist = "Thomas M. Baxa"
        flavorText = "\"Beware of trees that talk. Their words are threats. And mind the ones that sway and creak. They too threaten us, but in a foreign tongue.\"\n—*The Book of Other Folk*"
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b5ab941-89cc-4fdd-a916-3a54651f6478.jpg?1783942716"
    }
}
