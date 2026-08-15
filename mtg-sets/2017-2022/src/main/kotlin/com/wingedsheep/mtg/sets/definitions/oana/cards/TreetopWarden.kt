package com.wingedsheep.mtg.sets.definitions.oana.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Treetop Warden
 * {1}{G}
 * Creature — Elf Warrior
 * 2/2
 *
 * Vanilla — no rules text.
 */
val TreetopWarden = card("Treetop Warden") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Colin Boyer"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/771341f5-11b2-4edc-aa41-088e852c058e.jpg?1783934405"
    }
}
