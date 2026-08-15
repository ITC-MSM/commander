package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Meteor Golem reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * M19's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MeteorGolemReprint = Printing(
    oracleId = "d9f11aa1-9219-42a8-85a9-a8f204160706",
    name = "Meteor Golem",
    setCode = "CMR",
    collectorNumber = "325",
    artist = "Lake Hurwitz",
    imageUri = "https://cards.scryfall.io/normal/front/a/d/ad4b5324-bd25-4651-bcbc-5439b74df361.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
