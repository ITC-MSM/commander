package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Skittering Surveyor reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the MOM-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val SkitteringSurveyorReprint = Printing(
    oracleId = "85364398-923c-4ee9-8519-576fce1c26f7",
    name = "Skittering Surveyor",
    setCode = "MOM",
    collectorNumber = "264",
    scryfallId = "8bae9f1d-e96d-444c-a0fd-5608243ee6c8",
    artist = "Igor Kieryluk",
    imageUri = "https://cards.scryfall.io/normal/front/8/b/8bae9f1d-e96d-444c-a0fd-5608243ee6c8.jpg?1682205797",
    releaseDate = "2023-04-21",
    rarity = Rarity.COMMON,
)
