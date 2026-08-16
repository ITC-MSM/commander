package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Blood Artist reprint in J22. Canonical CardDefinition lives in Avacyn Restored (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.avr.cards.BloodArtist`.
 */
val BloodArtistReprint = Printing(
    oracleId = "310f141c-7f37-4729-aed6-dd9c09db448d",
    name = "Blood Artist",
    setCode = "J22",
    collectorNumber = "117",
    scryfallId = "c4c1641d-c4ea-4657-a0ae-db09bba83a0f",
    artist = "Julie Dillon",
    imageUri = "https://cards.scryfall.io/normal/front/c/4/c4c1641d-c4ea-4657-a0ae-db09bba83a0f.jpg?1783919145",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
