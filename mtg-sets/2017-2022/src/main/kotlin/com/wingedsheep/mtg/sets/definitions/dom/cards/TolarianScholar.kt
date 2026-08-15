package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tolarian Scholar
 * {2}{U}
 * Creature — Human Wizard
 * 2/3
 */
val TolarianScholar = card("Tolarian Scholar") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Sara Winters"
        flavorText = "The Tolarian Academies embrace a tradition of study and research while discouraging the kinds of experiments that ruined the original island of Tolaria."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/00d89839-60d7-4de2-a78a-1afdcc21c053.jpg?1562730535"
    }
}
