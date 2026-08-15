package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tobias Andrion
 * {3}{W}{U}
 * Legendary Creature — Human Advisor
 * 4/4
 *
 * Vanilla — no rules text.
 */
val TobiasAndrion = card("Tobias Andrion") {
    manaCost = "{3}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Advisor"
    power = 4
    toughness = 4

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "264"
        artist = "Andi Rusu"
        flavorText = "Administrator of the military state of Sheoltun, Tobias is the military right arm of the empire and the figurehead of its freedom."
        imageUri = "https://cards.scryfall.io/normal/front/c/a/cac56eda-5ed3-4abd-beec-f5063fbf930a.jpg?1783948031"
    }
}
