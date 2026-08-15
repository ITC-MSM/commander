package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Shattered Sanctum reprint in Secrets of Strixhaven. Canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Innistrad: Crimson Vow's
 * `cards/` package; this file contributes only presentation data.
 */
val ShatteredSanctumReprint = Printing(
    oracleId = "c854ecb0-cc60-4c48-a9aa-7f2348a7a8c6",
    name = "Shattered Sanctum",
    setCode = "SOS",
    collectorNumber = "260",
    scryfallId = "5aa0c810-3b7d-4661-979e-e84fb327742d",
    artist = "Sergey Glushakov",
    imageUri = "https://cards.scryfall.io/normal/front/5/a/5aa0c810-3b7d-4661-979e-e84fb327742d.jpg?1775938816",
    releaseDate = "2026-04-24",
    rarity = Rarity.RARE,
)
