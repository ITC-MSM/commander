package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nettle Swine
 * {3}{G}
 * Creature — Boar
 * 4/3
 *
 * Vanilla — no rules text.
 */
val NettleSwine = card("Nettle Swine") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Christopher Moeller"
        flavorText = "\"I killed one and found bricks and bones in its belly. It had eaten a whole cottage, thatch and all.\"\n—Paulin, Somberwald trapper"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75935f0e-9086-485b-b3e6-1a958fd0f2af.jpg?1783940664"
    }
}
