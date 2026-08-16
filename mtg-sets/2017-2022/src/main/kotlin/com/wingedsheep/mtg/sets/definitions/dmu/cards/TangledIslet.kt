package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Tangled Islet
 * Land — Forest Island
 * ({T}: Add {G} or {U}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val TangledIslet = card("Tangled Islet") {
    colorIdentity = "UG"
    typeLine = "Land — Forest Island"
    oracleText = "({T}: Add {G} or {U}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "258"
        artist = "Randy Gallegos"
        flavorText = "Many of Terisiare's islands are merely extensions of the root system of the Yavimaya magnigoth forests."
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a325fb4-fe71-47f1-9a2f-05b8cfe88fe6.jpg?1783921256"
    }
}
