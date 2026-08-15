package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jedit Ojanen
 * {4}{W}{W}{U}
 * Legendary Creature — Cat Warrior
 * 5/5
 *
 * Vanilla — no rules text.
 */
val JeditOjanen = card("Jedit Ojanen") {
    manaCost = "{4}{W}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Cat Warrior"
    power = 5
    toughness = 5

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Mark Poole"
        flavorText = "Mightiest of the Cat Warriors, Jedit Ojanen forsook the forests of his tribe and took service with the Robaran Mercenaries, rising through their ranks to lead them in the battle for Efrava."
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97b80124-2b59-425c-93cc-9b032e631c6e.jpg?1783948038"
    }
}
