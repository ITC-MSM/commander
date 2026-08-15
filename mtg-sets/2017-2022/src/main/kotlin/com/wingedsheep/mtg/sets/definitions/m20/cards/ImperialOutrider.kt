package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Imperial Outrider
 * {3}{W}
 * Creature — Human Knight
 * 1/5
 *
 * Vanilla — no rules text.
 */
val ImperialOutrider = card("Imperial Outrider") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 1
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "307"
        artist = "Scott Murphy"
        flavorText = "Her mount's hollow crest can produce a trumpeting warning that carries for miles, summoning more knights to her aid."
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0dd3aca5-516f-4500-9d7f-95630401d3ae.jpg?1783932913"
    }
}
