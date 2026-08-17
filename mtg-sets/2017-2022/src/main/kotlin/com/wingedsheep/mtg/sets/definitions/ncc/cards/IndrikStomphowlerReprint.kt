package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Indrik Stomphowler reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Dissension's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val IndrikStomphowlerReprint = Printing(
    oracleId = "de47a1e8-9c69-4af6-9d72-1bdd41352b32",
    name = "Indrik Stomphowler",
    setCode = "NCC",
    collectorNumber = "297",
    scryfallId = "1fa77af1-96e1-445b-a935-a5eb4c23e954",
    artist = "Carl Critchlow",
    imageUri = "https://cards.scryfall.io/normal/front/1/f/1fa77af1-96e1-445b-a935-a5eb4c23e954.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
