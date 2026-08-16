package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Overcome reprint in J22. Canonical CardDefinition lives in Hour of Devastation (its earliest real printing),
 * `com.wingedsheep.mtg.sets.definitions.hou.cards.Overcome`.
 */
val OvercomeReprint = Printing(
    oracleId = "15353e27-96ca-48c7-83d7-efd57c094643",
    name = "Overcome",
    setCode = "J22",
    collectorNumber = "701",
    scryfallId = "012f1dab-24c5-4bcd-bcc0-d282f28c8689",
    artist = "Craig J Spearing",
    imageUri = "https://cards.scryfall.io/normal/front/0/1/012f1dab-24c5-4bcd-bcc0-d282f28c8689.jpg?1783918851",
    releaseDate = "2022-12-02",
    rarity = Rarity.UNCOMMON,
)
