package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Nomad Outpost reprint in TDM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the TDM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val NomadOutpostReprint = Printing(
    oracleId = "4619de7e-3d6e-4c6b-8e6e-e24db324839d",
    name = "Nomad Outpost",
    setCode = "TDM",
    collectorNumber = "263",
    scryfallId = "a68fbeaa-941f-4d53-becd-f93ed22b9a54",
    artist = "Alayna Danner",
    imageUri = "https://cards.scryfall.io/normal/front/a/6/a68fbeaa-941f-4d53-becd-f93ed22b9a54.jpg?1743205041",
    releaseDate = "2025-04-11",
    rarity = Rarity.UNCOMMON,
)
