package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Radiant Grove
 * Land — Forest Plains
 * ({T}: Add {G} or {W}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val RadiantGrove = card("Radiant Grove") {
    colorIdentity = "WG"
    typeLine = "Land — Forest Plains"
    oracleText = "({T}: Add {G} or {W}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "253"
        artist = "Lorenzo Lanfranconi"
        flavorText = "Yavimaya's inhabitants are dedicated to protecting its unspoiled jungle from the scourge of artifice."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0f9dca6-e5ae-4714-8a59-2ef1d8f1e82d.jpg?1783921259"
    }
}
