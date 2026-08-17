package com.wingedsheep.mtg.sets.definitions.msc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Propaganda reprint in MSC. The canonical CardDefinition lives in
 * Tempest (`tmp`), the card's earliest real printing; this file
 * contributes only per-printing presentation data.
 */
val PropagandaReprint = Printing(
    oracleId = "ea9709b6-4c37-4d5a-b04d-cd4c42e4f9dd",
    name = "Propaganda",
    setCode = "MSC",
    collectorNumber = "151",
    scryfallId = "ac943e31-26bc-4b54-b73f-460e6e402d86",
    artist = "Borja Pindado",
    imageUri = "https://cards.scryfall.io/normal/front/a/c/ac943e31-26bc-4b54-b73f-460e6e402d86.jpg?1783903239",
    releaseDate = "2026-06-26",
    rarity = Rarity.UNCOMMON,
    frameEffects = listOf("enchantment"),
)
