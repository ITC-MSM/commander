package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Brineborn Cutthroat reprint in Jumpstart 2022.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in M20's
 * `cards/` package (the card's earliest real printing). This file contributes only the
 * J22-specific presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BrinebornCutthroatReprint = Printing(
    oracleId = "916cb70f-3b06-48ed-972d-75f805aa0892",
    name = "Brineborn Cutthroat",
    setCode = "J22",
    collectorNumber = "278",
    scryfallId = "9021592a-1170-4e8e-a9bd-3086bbc0d435",
    artist = "Caio Monteiro",
    imageUri = "https://cards.scryfall.io/normal/front/9/0/9021592a-1170-4e8e-a9bd-3086bbc0d435.jpg?1782699188",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
