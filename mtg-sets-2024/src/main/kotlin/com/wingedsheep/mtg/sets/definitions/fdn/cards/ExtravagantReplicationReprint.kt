package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Extravagant Replication reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types) lives in New
 * Capenna Commander's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ExtravagantReplicationReprint = Printing(
    oracleId = "3a646245-b8b7-4f91-a312-d5eea9a9e49a",
    name = "Extravagant Replication",
    setCode = "FDN",
    collectorNumber = "154",
    scryfallId = "6a41dfae-bc7e-4105-8f7e-fd0109197ad8",
    artist = "Pauline Voss",
    imageUri = "https://cards.scryfall.io/normal/front/6/a/6a41dfae-bc7e-4105-8f7e-fd0109197ad8.jpg?1783909081",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
