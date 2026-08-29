package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Jace, the Mind Sculptor reprints in Bloomburrow Commander — the deck printing (#75) and the
 * borderless one (#93). The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in the
 * Worldwake (`wwk`) `cards/` package; these contribute only per-printing presentation data.
 */
val JaceTheMindSculptorReprint = Printing(
    oracleId = "7f77a84e-5a4b-4834-aefa-3cecc175ae8e",
    name = "Jace, the Mind Sculptor",
    setCode = "BLC",
    collectorNumber = "75",
    scryfallId = "cbe639bb-5797-4bec-b55b-aed296337e92",
    artist = "Justin Gerard",
    imageUri = "https://cards.scryfall.io/normal/front/c/b/cbe639bb-5797-4bec-b55b-aed296337e92.jpg?1783910714",
    releaseDate = "2024-08-02",
    rarity = Rarity.MYTHIC,
)

val JaceTheMindSculptorBorderlessReprint = Printing(
    oracleId = "7f77a84e-5a4b-4834-aefa-3cecc175ae8e",
    name = "Jace, the Mind Sculptor",
    setCode = "BLC",
    collectorNumber = "93",
    scryfallId = "97c67e86-5aa5-4136-a15c-c0c5704e2b94",
    artist = "TAPIOCA",
    imageUri = "https://cards.scryfall.io/normal/front/9/7/97c67e86-5aa5-4136-a15c-c0c5704e2b94.jpg?1783910708",
    releaseDate = "2024-08-02",
    rarity = Rarity.MYTHIC,
)
