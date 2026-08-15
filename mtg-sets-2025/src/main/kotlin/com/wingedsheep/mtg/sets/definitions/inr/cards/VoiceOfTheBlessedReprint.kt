package com.wingedsheep.mtg.sets.definitions.inr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Voice of the Blessed reprint in INR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * VOW's `cards/` package (the card's earliest real printing). This file contributes only
 * the INR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val VoiceOfTheBlessedReprint = Printing(
    oracleId = "01e742ed-660c-43c6-b393-e589a54cbe1b",
    name = "Voice of the Blessed",
    setCode = "INR",
    collectorNumber = "50",
    artist = "Anastasia Ovchinnikova",
    imageUri = "https://cards.scryfall.io/normal/front/6/2/62534128-2a85-4a36-bc7b-e4f4c51fa1f6.jpg",
    releaseDate = "2025-01-24",
    rarity = Rarity.RARE,
)
