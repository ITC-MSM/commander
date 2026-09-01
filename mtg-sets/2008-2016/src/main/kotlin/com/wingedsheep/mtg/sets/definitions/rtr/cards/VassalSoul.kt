package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vassal Soul
 * {1}{W/U}{W/U}
 * Creature — Spirit
 * 2/2
 *
 * Flying
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * One evergreen keyword and nothing else. The hybrid mana cost is data on the card, not script.
 */
val VassalSoul = card("Vassal Soul") {
    manaCost = "{1}{W/U}{W/U}"
    colorIdentity = "UW"
    typeLine = "Creature — Spirit"
    oracleText = "Flying"
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "224"
        artist = "Dan Murayama Scott"
        flavorText = "For the Azorius, the opportunity to serve the law is too great an honor for death to interrupt."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfc61748-029f-4bae-a7ec-e08b7059226d.jpg?1783940325"
    }
}
