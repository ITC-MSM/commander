package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Initiate's Companion reprint in Jumpstart. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Amonkhet's `cards/` package; this file contributes only presentation data.
 */
val InitiatesCompanionJmpReprint = Printing(
    oracleId = "f3264224-43a4-4c6b-b363-2d147687dfc1",
    name = "Initiate's Companion",
    setCode = "JMP",
    collectorNumber = "403",
    scryfallId = "380e83c1-e5e3-49b2-bbf3-fad8cc7d020a",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/3/8/380e83c1-e5e3-49b2-bbf3-fad8cc7d020a.jpg?1783930362",
    releaseDate = "2020-07-17",
    rarity = Rarity.COMMON,
)
