package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Izzet Chronarch reprint in Commander 2011. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Guildpact's `cards/` package; this file
 * contributes only the Commander 2011 presentation row.
 */
val IzzetChronarchReprint = Printing(
    oracleId = "1da438f3-db1c-4713-a60c-e078f31d809c",
    name = "Izzet Chronarch",
    setCode = "CMD",
    collectorNumber = "205",
    scryfallId = "29681b0d-1aed-4b50-9710-9c15f3c83c72",
    artist = "Nick Percival",
    imageUri = "https://cards.scryfall.io/normal/front/2/9/29681b0d-1aed-4b50-9710-9c15f3c83c72.jpg?1592714111",
    releaseDate = "2011-06-17",
    rarity = Rarity.COMMON,
)
