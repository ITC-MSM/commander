package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Self-Assembler reprint in J22. Canonical CardDefinition lives in Kaladesh (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.kld.cards.SelfAssembler`.
 */
val SelfAssemblerReprint = Printing(
    oracleId = "8f36e058-e5fa-48f9-9996-09b77fc193b3",
    name = "Self-Assembler",
    setCode = "J22",
    collectorNumber = "795",
    scryfallId = "02e77f29-8ce1-4a18-871c-d69853bfd9db",
    artist = "Toraji",
    imageUri = "https://cards.scryfall.io/normal/front/0/2/02e77f29-8ce1-4a18-871c-d69853bfd9db.jpg?1783918799",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
