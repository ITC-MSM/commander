package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Artisan of Kozilek reprint in New Capenna Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in Rise of the Eldrazi's `cards/` package; this
 * file contributes only the New Capenna Commander presentation row.
 */
val ArtisanOfKozilekReprint = Printing(
    oracleId = "19409704-09c4-4a4b-a5a7-f95120b425db",
    name = "Artisan of Kozilek",
    setCode = "NCC",
    collectorNumber = "191",
    scryfallId = "e3fef480-ee0e-41e1-9db3-68e945e7e867",
    artist = "Jason Felix",
    imageUri = "https://cards.scryfall.io/normal/front/e/3/e3fef480-ee0e-41e1-9db3-68e945e7e867.jpg",
    releaseDate = "2022-04-29",
    rarity = Rarity.UNCOMMON,
)
