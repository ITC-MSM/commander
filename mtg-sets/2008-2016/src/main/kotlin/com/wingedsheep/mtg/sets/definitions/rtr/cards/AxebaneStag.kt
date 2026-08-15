package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Axebane Stag
 * {6}{G}
 * Creature — Elk
 * 6/7
 *
 * Vanilla — no rules text.
 */
val AxebaneStag = card("Axebane Stag") {
    manaCost = "{6}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elk"
    power = 6
    toughness = 7

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Martina Pilcerova"
        flavorText = "\"When the spires have burned and the cobblestones are dust, he will take his rightful place as king of the wilds.\"\n—Kirce, Axebane guardian"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bfce7c02-ccc3-44cd-8087-627eaa6a072e.jpg?1783940351"
    }
}
