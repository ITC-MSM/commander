package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Felhide Minotaur
 * {2}{B}
 * Creature — Minotaur
 * 2/3
 *
 * Vanilla — no rules text.
 */
val FelhideMinotaur = card("Felhide Minotaur") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Minotaur"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Kev Walker"
        flavorText = "With spear held high, the Champion came to meet Thyrogog of the Ashlands, who wore the old king's skin as a cloak and fed on the flesh of innocents. The foul minotaur raised the great axe called Goremaster and charged.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4e424de-81be-4f90-a7a2-4102c8ba8989.jpg?1783939779"
    }
}
