package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mirrodin's Core reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Darksteel's `cards/` package; this file contributes only presentation data.
 */
val MirrodinsCoreJmpReprint = Printing(
    oracleId = "ea53adbe-3f9a-4847-87c7-723ac2789918",
    name = "Mirrodin's Core",
    setCode = "JMP",
    collectorNumber = "492",
    scryfallId = "c0a9fbb3-9fe4-4ec6-82f0-3bb101524e1e",
    artist = "Greg Staples",
    imageUri = "https://cards.scryfall.io/normal/front/c/0/c0a9fbb3-9fe4-4ec6-82f0-3bb101524e1e.jpg?1783930329",
    releaseDate = "2020-07-17",
    rarity = Rarity.UNCOMMON,
)
