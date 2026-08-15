package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Scoured Barrens reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ScouredBarrensReprint = Printing(
    oracleId = "d37f858e-03c8-4594-9b92-cd03699a1591",
    name = "Scoured Barrens",
    setCode = "MOM",
    collectorNumber = "272",
    scryfallId = "66aefbfc-3f67-443d-8ec4-cc9beafb64ee",
    artist = "Robin Olausson",
    imageUri = "https://cards.scryfall.io/normal/front/6/6/66aefbfc-3f67-443d-8ec4-cc9beafb64ee.jpg?1682205882",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
