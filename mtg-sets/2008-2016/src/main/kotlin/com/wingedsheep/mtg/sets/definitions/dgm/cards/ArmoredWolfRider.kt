package com.wingedsheep.mtg.sets.definitions.dgm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Wolf-Rider
 * {3}{G}{W}
 * Creature — Elf Knight
 * 4/6
 *
 * Vanilla — no rules text.
 */
val ArmoredWolfRider = card("Armored Wolf-Rider") {
    manaCost = "{3}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Creature — Elf Knight"
    power = 4
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "Matt Stewart"
        flavorText = "Wolf-riders of Selesnya apprentice from a young age. Each rider raises a wolf pup from birth to serve as a mount, hoping that one day the two will share the honor of guarding the Great Concourse."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e43d959f-6055-4578-a69a-0ec93e993e21.jpg?1783940033"
    }
}
