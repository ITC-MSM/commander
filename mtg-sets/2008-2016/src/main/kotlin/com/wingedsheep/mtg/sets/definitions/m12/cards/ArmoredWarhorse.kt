package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Warhorse
 * {W}{W}
 * Creature — Horse
 * 2/3
 *
 * Vanilla — no rules text.
 */
val ArmoredWarhorse = card("Armored Warhorse") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Horse"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "rk post"
        flavorText = "\"When we of the Northern Verge claim a mount, no peasant's nag will do. It must be as strong as our virtue, and must join us of its own will.\"\n—Sarlena, paladin of the Northern Verge"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52daf505-d436-4ea6-a157-4268af2ff7a8.jpg?1783941106"
    }
}
