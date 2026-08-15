package com.wingedsheep.mtg.sets.definitions.csp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Krovikan Scoundrel
 * {1}{B}
 * Creature — Human Rogue
 * 2/1
 *
 * Vanilla — no rules text.
 */
val KrovikanScoundrel = card("Krovikan Scoundrel") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "64"
        artist = "Ralph Horsley"
        flavorText = "The few surviving humans of Krov would have welcomed the return of winter and its sterilizing cold. They often peered northward, hoping that the snow's edge had reached their infested city-tomb."
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd9f046c-b416-4d80-8998-047b98361352.jpg?1783943348"
    }
}
