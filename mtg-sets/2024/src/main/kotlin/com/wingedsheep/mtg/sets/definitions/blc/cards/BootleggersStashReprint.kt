package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Bootleggers' Stash reprint in Bloomburrow Commander. Canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in `definitions/snc/cards/BootleggersStash.kt`;
 * this file contributes only presentation data.
 */
val BootleggersStashReprint = Printing(
    oracleId = "d1deba03-b26a-4a0d-b242-24bb3a3f9ea6",
    name = "Bootleggers' Stash",
    setCode = "BLC",
    collectorNumber = "207",
    scryfallId = "c10525b5-067c-4a30-a069-875f11f2ff19",
    artist = "Anastasia Ovchinnikova",
    imageUri = "https://cards.scryfall.io/normal/front/c/1/c10525b5-067c-4a30-a069-875f11f2ff19.jpg?1721429213",
    releaseDate = "2024-08-02",
    rarity = Rarity.MYTHIC,
)
