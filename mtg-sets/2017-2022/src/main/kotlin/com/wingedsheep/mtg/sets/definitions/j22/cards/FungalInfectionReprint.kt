package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Fungal Infection reprint in J22. Canonical CardDefinition lives in Dominaria (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.dom.cards.FungalInfection`.
 */
val FungalInfectionReprint = Printing(
    oracleId = "04a8858e-9033-403e-9346-5a2ad1bdf680",
    name = "Fungal Infection",
    setCode = "J22",
    collectorNumber = "416",
    scryfallId = "727a589a-2248-478f-825f-932bfb456675",
    artist = "Filip Burburan",
    imageUri = "https://cards.scryfall.io/normal/front/7/2/727a589a-2248-478f-825f-932bfb456675.jpg?1783919005",
    releaseDate = "2022-12-02",
    rarity = Rarity.COMMON,
)
