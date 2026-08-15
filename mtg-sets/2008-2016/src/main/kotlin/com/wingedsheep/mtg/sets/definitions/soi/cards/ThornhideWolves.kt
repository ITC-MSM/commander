package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thornhide Wolves
 * {4}{G}
 * Creature — Wolf
 * 4/5
 *
 * Vanilla — no rules text.
 */
val ThornhideWolves = card("Thornhide Wolves") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 4
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "232"
        artist = "Scott Murphy"
        flavorText = "\"Halana grew brambles to create a barricade around our camp, hoping that it would keep the wolves out. That was a mistake for which we almost paid dearly.\"\n—Alena, trapper of Kessig"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c699b5f-a2de-4c87-a7bc-16be4bc0a8cd.jpg?1783937720"
    }
}
