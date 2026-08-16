package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Contaminated Aquifer
 * Land — Island Swamp
 * ({T}: Add {U} or {B}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val ContaminatedAquifer = card("Contaminated Aquifer") {
    colorIdentity = "UB"
    typeLine = "Land — Island Swamp"
    oracleText = "({T}: Add {U} or {B}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "245"
        artist = "Robin Olausson"
        flavorText = "The caves stretch farther than anyone knows, and the terrible legacy of Yawgmoth lurks in every shadow."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6bb570f4-de68-4d8c-a9f3-8a3294163aa8.jpg?1783921262"
    }
}
