package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Angelic Destiny reprint in FDN. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Magic 2012 (its earliest printing);
 * this file contributes only the FDN presentation row.
 */
val AngelicDestinyReprint = Printing(
    oracleId = "8ba36878-0816-44c7-b543-8ebfc28c2ecd",
    name = "Angelic Destiny",
    setCode = "FDN",
    collectorNumber = "565",
    scryfallId = "066d73fa-369b-44a3-b1a9-d22176ac3566",
    artist = "Jana Schirmer & Johannes Voss",
    imageUri = "https://cards.scryfall.io/normal/front/0/6/066d73fa-369b-44a3-b1a9-d22176ac3566.jpg?1783908943",
    releaseDate = "2024-11-15",
    rarity = Rarity.MYTHIC,
)
