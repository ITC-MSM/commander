package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Crooked Custodian
 * {1}{B}
 * Creature — Ogre Rogue
 * 3 / 2
 * This creature enters tapped.
 */
val CrookedCustodian = card("Crooked Custodian") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Ogre Rogue"
    oracleText = "This creature enters tapped."
    power = 3
    toughness = 2

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Tony Foti"
        flavorText = "\"Nothing to see here. Just carrying a carpet. Yes, the carpet wears boots. Stop asking questions.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/2/723d6f60-3e8a-4c58-8b3c-9ba59a01c867.jpg?1783923135"
    }
}
