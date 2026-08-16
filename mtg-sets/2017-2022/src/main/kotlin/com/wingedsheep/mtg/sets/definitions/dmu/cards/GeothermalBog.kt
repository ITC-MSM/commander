package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Geothermal Bog
 * Land — Swamp Mountain
 * ({T}: Add {B} or {R}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val GeothermalBog = card("Geothermal Bog") {
    colorIdentity = "BR"
    typeLine = "Land — Swamp Mountain"
    oracleText = "({T}: Add {B} or {R}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "247"
        artist = "Gabor Szikszai"
        flavorText = "There are still a few places where the natural dangers outweigh even those posed by Phyrexian sleeper agents."
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e62fe57-4fa4-4bfd-8e31-9f6db774d7e2.jpg?1783921261"
    }
}
