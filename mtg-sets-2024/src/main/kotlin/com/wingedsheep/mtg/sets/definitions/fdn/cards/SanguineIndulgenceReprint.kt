package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Sanguine Indulgence reprint in FDN. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Core Set 2021 (its earliest printing);
 * this file contributes only the FDN presentation row.
 */
val SanguineIndulgenceReprint = Printing(
    oracleId = "010ec11a-0484-486d-b7c0-de65593bf457",
    name = "Sanguine Indulgence",
    setCode = "FDN",
    collectorNumber = "613",
    scryfallId = "03a26ca7-b26f-4b86-a060-712f15f4bf7f",
    artist = "Andrey Kuzinskiy",
    imageUri = "https://cards.scryfall.io/normal/front/0/3/03a26ca7-b26f-4b86-a060-712f15f4bf7f.jpg?1783908926",
    releaseDate = "2024-11-15",
    rarity = Rarity.COMMON,
)
