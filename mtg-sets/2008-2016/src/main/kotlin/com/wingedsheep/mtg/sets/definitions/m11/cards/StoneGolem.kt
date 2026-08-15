package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stone Golem
 * {5}
 * Artifact Creature — Golem
 * 4/4
 *
 * Vanilla — no rules text.
 */
val StoneGolem = card("Stone Golem") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Martina Pilcerova"
        flavorText = "The sculptor, like most artists, put his heart and soul in his work. But the newly awakened golem decided he wanted his creator's other, more tangible parts."
        imageUri = "https://cards.scryfall.io/normal/front/3/7/37d0876b-5026-4a0a-bf2f-e831593643a7.jpg?1783941788"
    }
}
