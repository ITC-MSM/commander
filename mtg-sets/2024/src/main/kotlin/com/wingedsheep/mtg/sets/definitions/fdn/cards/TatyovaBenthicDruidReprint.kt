package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tatyova, Benthic Druid reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TatyovaBenthicDruidReprint = Printing(
    oracleId = "0715e860-3b3b-4331-9718-207973e94fee",
    name = "Tatyova, Benthic Druid",
    setCode = "FDN",
    collectorNumber = "247",
    scryfallId = "eabc978a-0666-472d-bdc6-d4b29d29eca4",
    artist = "Mathias Kollros",
    imageUri = "https://cards.scryfall.io/normal/front/e/a/eabc978a-0666-472d-bdc6-d4b29d29eca4.jpg?1730489527",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
