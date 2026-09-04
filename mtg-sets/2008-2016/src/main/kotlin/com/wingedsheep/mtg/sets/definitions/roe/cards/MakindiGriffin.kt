package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Makindi Griffin
 * {3}{W}
 * Creature — Griffin
 * 2 / 4
 *
 * Flying
 *
 * Modeling notes:
 *  - Vanilla flyer; a single `keywords(Keyword.FLYING)` declaration covers the printed line.
 */
val MakindiGriffin = card("Makindi Griffin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    power = 2
    toughness = 4
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Izzy"
        flavorText = "As the hedrons began to coalesce into colossal Eldrazi superstructures, the griffins were forced to seek new territory lest their aeries be crushed between the massive stone monoliths."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f839a5e-3cbb-4179-9267-02f40645bdbc.jpg?1783942005"
    }
}
