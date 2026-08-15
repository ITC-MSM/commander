package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Savai Sabertooth
 * {1}{W}
 * Creature — Cat
 * 3/1
 *
 * Vanilla — no rules text.
 */
val SavaiSabertooth = card("Savai Sabertooth") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Ilse Gort"
        flavorText = "The giant tigers of Savai prefer to hunt at daybreak, when the crystals' warning glow is masked by the light of dawn."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d46702b0-7e10-462a-9aac-7564efe91804.jpg?1783931085"
    }
}
