package com.wingedsheep.mtg.sets.definitions.trc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Starfleet Crew
 * {1}{W}
 * Creature — Officer
 * 2/3
 *
 * Vanilla — no rules text.
 */
val StarfleetCrew = card("Starfleet Crew") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Officer"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Runa I. Rosenberger"
        flavorText = "\"The first duty of every Starfleet officer is to the truth, whether it's scientific truth, or historical truth, or personal truth. It is the guiding principle on which Starfleet is based.\"\n—Captain Jean-Luc Picard"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ecf2358f-542d-45c9-b5c5-c57b2b0c1122.jpg?1785981010"
    }
}
