package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Vivien Reid reprints in Foundations (FDN).
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, loyalty abilities) lives in
 * the M19 `cards/` package — its earliest real printing. These files contribute only the
 * FDN-specific presentation rows (set, collector number, art), picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 *
 * FDN prints Vivien Reid three times: the main-set #234 (Anna Steinbauer) and two full-art
 * variants #361 and #421 (Zara Alfonso).
 */
val VivienReidReprint = Printing(
    oracleId = "b5f20a6d-8c3e-452f-9d98-4886f0fa052a",
    name = "Vivien Reid",
    setCode = "FDN",
    collectorNumber = "234",
    scryfallId = "38769247-42a1-4571-9071-6d59fe28650a",
    artist = "Anna Steinbauer",
    imageUri = "https://cards.scryfall.io/normal/front/3/8/38769247-42a1-4571-9071-6d59fe28650a.jpg?1730489472",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)

val VivienReidReprintFullArt361 = Printing(
    oracleId = "b5f20a6d-8c3e-452f-9d98-4886f0fa052a",
    name = "Vivien Reid",
    setCode = "FDN",
    collectorNumber = "361",
    scryfallId = "37cc3e80-2ffa-4d48-bd67-a6315375b236",
    artist = "Zara Alfonso",
    imageUri = "https://cards.scryfall.io/normal/front/3/7/37cc3e80-2ffa-4d48-bd67-a6315375b236.jpg?1730718627",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
    isFullArt = true,
)

val VivienReidReprintFullArt421 = Printing(
    oracleId = "b5f20a6d-8c3e-452f-9d98-4886f0fa052a",
    name = "Vivien Reid",
    setCode = "FDN",
    collectorNumber = "421",
    scryfallId = "162a3a3b-257e-46c7-bb29-65627b00363d",
    artist = "Zara Alfonso",
    imageUri = "https://cards.scryfall.io/normal/front/1/6/162a3a3b-257e-46c7-bb29-65627b00363d.jpg?1734454679",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
    isFullArt = true,
)
