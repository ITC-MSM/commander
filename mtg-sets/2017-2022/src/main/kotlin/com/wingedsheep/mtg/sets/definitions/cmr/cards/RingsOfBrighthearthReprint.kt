package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Rings of Brighthearth reprint in Commander Legends. The canonical CardDefinition lives in Lorwyn
 * (`lrw`), the card's earliest real printing; this file contributes only per-printing presentation
 * data. Commander Legends printed it twice — the draft-frame #335 and the extended-art #698 — and
 * printings are keyed by `(setCode, collectorNumber)`, so each number needs its own row.
 */
val RingsOfBrighthearthReprint = Printing(
    oracleId = "bbf9494c-c4bb-4d36-98fe-8387846b342e",
    name = "Rings of Brighthearth",
    setCode = "CMR",
    collectorNumber = "335",
    scryfallId = "838ffc87-517a-4d94-8ce0-bc9ed01ecc52",
    artist = "Howard Lyon",
    imageUri = "https://cards.scryfall.io/normal/front/8/3/838ffc87-517a-4d94-8ce0-bc9ed01ecc52.jpg?1783928748",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
)

/** The extended-art printing of the same card in the same set. */
val RingsOfBrighthearthExtendedArtReprint = Printing(
    oracleId = "bbf9494c-c4bb-4d36-98fe-8387846b342e",
    name = "Rings of Brighthearth",
    setCode = "CMR",
    collectorNumber = "698",
    scryfallId = "1dbc00c6-5df2-4953-95c2-6c79dc86f409",
    artist = "Howard Lyon",
    imageUri = "https://cards.scryfall.io/normal/front/1/d/1dbc00c6-5df2-4953-95c2-6c79dc86f409.jpg?1783928597",
    releaseDate = "2020-11-20",
    rarity = Rarity.RARE,
    frameEffects = listOf("extendedart"),
)
