package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hexplate Golem
 * {7}
 * Artifact Creature — Golem
 * 5/7
 *
 * Vanilla — no rules text.
 */
val HexplateGolem = card("Hexplate Golem") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 5
    toughness = 7

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Matt Cavotta"
        flavorText = "\"Use everything. Iron, rust, scrap . . . even the ground must join our cause.\"\n—Ezuri, renegade leader"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/49b913f3-6581-45ae-9cdb-274c2ccd8899.jpg?1783941369"
    }
}
