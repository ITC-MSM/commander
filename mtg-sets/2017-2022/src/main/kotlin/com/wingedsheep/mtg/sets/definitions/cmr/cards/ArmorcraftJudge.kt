package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Armorcraft Judge reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * KLD's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ArmorcraftJudgeReprint = Printing(
    oracleId = "d7f49243-a96e-499f-b2d7-8e9842432420",
    name = "Armorcraft Judge",
    setCode = "CMR",
    collectorNumber = "218",
    artist = "David Palumbo",
    imageUri = "https://cards.scryfall.io/normal/front/4/e/4e1483d4-bb91-4bf9-a57f-dd46fa31056b.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
