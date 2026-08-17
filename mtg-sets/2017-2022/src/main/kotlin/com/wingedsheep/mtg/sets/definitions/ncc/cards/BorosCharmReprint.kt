package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Boros Charm reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Gatecrash's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val BorosCharmReprint = Printing(
    oracleId = "2679d0dd-ba30-4a1c-b6a0-b3ac6c790496",
    name = "Boros Charm",
    setCode = "NCC",
    collectorNumber = "332",
    scryfallId = "437b0685-ed50-40b9-ae0d-ec2f75026474",
    artist = "Zoltan Boros",
    imageUri = "https://cards.scryfall.io/normal/front/4/3/437b0685-ed50-40b9-ae0d-ec2f75026474.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
