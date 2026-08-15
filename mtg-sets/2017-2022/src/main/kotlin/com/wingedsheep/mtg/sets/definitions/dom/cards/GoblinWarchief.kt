package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Warchief reprint in DOM.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * SCG's `cards/` package (the card's earliest real printing). This file contributes
 * only the DOM-specific presentation row.
 */
val GoblinWarchiefReprint = Printing(
    oracleId = "39882df0-c20f-469d-94ba-1617224e71a1",
    name = "Goblin Warchief",
    setCode = "DOM",
    collectorNumber = "130",
    scryfallId = "5bac033c-dc4e-40a0-b103-4892e4b50249",
    artist = "Karl Kopinski",
    imageUri = "https://cards.scryfall.io/normal/front/5/b/5bac033c-dc4e-40a0-b103-4892e4b50249.jpg?1562736294",
    releaseDate = "2018-04-27",
    rarity = Rarity.UNCOMMON,
)
