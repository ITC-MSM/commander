package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vulpine Goliath
 * {4}{G}{G}
 * Creature — Fox
 * 6 / 5
 *
 * Trample
 */
val VulpineGoliath = card("Vulpine Goliath") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fox"
    power = 6
    toughness = 5
    oracleText = "Trample"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Adam Paquette"
        flavorText = "\"With a diet of hydras, giants, and massive serpents, anything would get that big.\"\n—Corisande, Setessan hunter"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdacb147-35ce-4751-961e-576b5f958048.jpg"
    }
}
