package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nyxborn Brute
 * {3}{R}{R}
 * Enchantment Creature — Cyclops
 * 7/3
 *
 * Vanilla — no rules text.
 */
val NyxbornBrute = card("Nyxborn Brute") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment Creature — Cyclops"
    power = 7
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Zoltan Boros"
        flavorText = "\"One-eyed and frightful, the cyclops\nlifted a boulder and hurled it\nseaward from cliff's edge, shattering\nmasts and scattering sailors.\"\n—*The Callapheia*"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05bc4236-566f-401b-b9d7-f58126fa228b.jpg?1783931550"
    }
}
