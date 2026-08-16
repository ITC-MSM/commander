package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Monastery Mentor reprint in MOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Fate Reforged (`frf`). This
 * file contributes only the MOM-specific presentation row — set, collector number, art.
 */
val MonasteryMentorReprint = Printing(
    oracleId = "3979067a-9c68-443d-a85f-d9f07be880b9",
    name = "Monastery Mentor",
    setCode = "MOM",
    collectorNumber = "28",
    scryfallId = "75665c2f-a100-4e3f-be8e-b5cc3c9a090b",
    artist = "Brian Valeza",
    imageUri = "https://cards.scryfall.io/normal/front/7/5/75665c2f-a100-4e3f-be8e-b5cc3c9a090b.jpg?1783917054",
    releaseDate = "2023-04-21",
    rarity = Rarity.MYTHIC,
)
