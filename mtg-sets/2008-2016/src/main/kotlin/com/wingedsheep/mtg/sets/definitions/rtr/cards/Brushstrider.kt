package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brushstrider
 * {1}{G}
 * Creature — Beast
 * 3/1
 *
 * Vigilance (Attacking doesn't cause this creature to tap.)
 */
val Brushstrider = card("Brushstrider") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)"
    power = 3
    toughness = 1

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Raoul Vitale"
        flavorText = "Magistrate Ludy agreed to designate land for the brushstriders only after several broken windows and dozens of missing blini-cakes."
        imageUri = "https://cards.scryfall.io/normal/front/5/9/59bd1534-52d1-4946-b430-d26f039a9067.jpg?1783940350"
    }
}
