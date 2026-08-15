package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Undead Minotaur
 * {2}{B}
 * Creature — Zombie Minotaur
 * 2/3
 *
 * Vanilla — no rules text.
 */
val UndeadMinotaur = card("Undead Minotaur") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Minotaur"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Karl Kopinski"
        flavorText = "\"The work that went into creating this magnificent specimen. Horrific, deadly, well-balanced. Not all necromancers do elegant work like this.\"\n—Lestin, necromancer"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e5ae910-ee1d-4958-92d9-0b06872913c6.jpg?1783939918"
    }
}
