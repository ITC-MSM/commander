package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mild-Mannered Librarian reprint in Foundations.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in J22's `cards/` package
 * (the card's earliest real printing). This file contributes only the FDN presentation row.
 */
val MildManneredLibrarianReprint = Printing(
    oracleId = "fb7070ab-c234-4d54-8cdc-caf106bf24a6",
    name = "Mild-Mannered Librarian",
    setCode = "FDN",
    collectorNumber = "228",
    scryfallId = "5389663a-fe25-41b9-8c92-1f4d7721ffc2",
    artist = "Justyna Dura",
    imageUri = "https://cards.scryfall.io/normal/front/5/3/5389663a-fe25-41b9-8c92-1f4d7721ffc2.jpg?1782689070",
    releaseDate = "2024-11-15",
    rarity = Rarity.UNCOMMON,
)
