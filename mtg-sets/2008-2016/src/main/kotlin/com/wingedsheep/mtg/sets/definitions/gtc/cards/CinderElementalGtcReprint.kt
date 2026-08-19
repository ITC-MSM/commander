package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Cinder Elemental reprint in Gatecrash. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives
 * in another set's `cards/` package; this file contributes only presentation data.
 */
val CinderElementalGtcReprint = Printing(
    oracleId = "01805d0d-3a72-4d26-9475-3e68c278b7fe",
    name = "Cinder Elemental",
    setCode = "GTC",
    collectorNumber = "87",
    scryfallId = "8bbf10ce-69e0-4984-91a3-f65df919830d",
    artist = "Svetlin Velinov",
    imageUri = "https://cards.scryfall.io/normal/front/8/b/8bbf10ce-69e0-4984-91a3-f65df919830d.jpg?1783940125",
    releaseDate = "2013-02-01",
    rarity = Rarity.UNCOMMON,
)
