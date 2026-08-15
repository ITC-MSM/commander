package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jwari Scuttler
 * {2}{U}
 * Creature — Crab
 * 2/3
 *
 * Vanilla — no rules text.
 */
val JwariScuttler = card("Jwari Scuttler") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Andrew Robinson"
        flavorText = "\"Yeah, they've got a lot of meat. The only downside to eating 'em is that you often find human bones and body parts inside.\"\n—Jaby, Silundi Sea nomad"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04129038-3b02-418a-862a-229e9dde339b.jpg?1783941995"
    }
}
