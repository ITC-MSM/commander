package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wary Okapi
 * {2}{G}
 * Creature — Antelope
 * 3/2
 * Vigilance
 */
val WaryOkapi = card("Wary Okapi") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Antelope"
    oracleText = "Vigilance"
    power = 3
    toughness = 2

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Jason Felix"
        flavorText = "\"Be like the grazers of the Saruli. Keep your herd close, and stay alert for encroaching danger.\"\n—Lalia, Selesnya dryad"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54f26697-0d4b-4af4-a644-3d0ae13f1d2e.jpg?1783934144"
    }
}
