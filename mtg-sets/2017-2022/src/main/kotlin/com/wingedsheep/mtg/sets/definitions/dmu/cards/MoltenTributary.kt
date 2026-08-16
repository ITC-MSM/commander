package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Molten Tributary
 * Land — Island Mountain
 * ({T}: Add {U} or {R}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val MoltenTributary = card("Molten Tributary") {
    colorIdentity = "UR"
    typeLine = "Land — Island Mountain"
    oracleText = "({T}: Add {U} or {R}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "251"
        artist = "Yeong-Hao Han"
        flavorText = "Though dangerous, Shiv teems with both life and mana, making it a tempting destination for ambitious mages."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20aff4af-5128-432f-a8c8-65b6909d31ac.jpg?1783921259"
    }
}
