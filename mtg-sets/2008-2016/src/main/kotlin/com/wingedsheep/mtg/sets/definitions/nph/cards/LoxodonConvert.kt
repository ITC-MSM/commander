package com.wingedsheep.mtg.sets.definitions.nph.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Loxodon Convert
 * {3}{W}
 * Creature — Phyrexian Elephant Soldier
 * 4/2
 *
 * Vanilla — no rules text.
 */
val LoxodonConvert = card("Loxodon Convert") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Phyrexian Elephant Soldier"
    power = 4
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Adrian Smith"
        flavorText = "Just one drop of the glistening oil can eventually stain even a soul as stalwart as a loxodon's beyond redemption."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/00c050c3-4f50-4bb6-8477-6737887ca10d.jpg?1783941325"
    }
}
